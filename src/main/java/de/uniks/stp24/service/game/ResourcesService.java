package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class ResourcesService {
 	@Inject
    GameSystemsApiService gameSystemsApiService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public EmpireService empireService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;

    /**
     * storage for actual resources
     */
    private Map<String, Integer> currentResources = new HashMap<>();

    @Inject
    public ResourcesService() {
    }

    public int getResourceCount(String resourceId) {
        return currentResources.get(resourceId);
    }

    public Observable<SystemDto> destroyBuilding(String gameID, Island island, String buildingToDestroy) {
        ArrayList<String> buildings = island.buildings();


        // Look in list for building to delete
        Iterator<String> iterator = buildings.iterator();
        while (iterator.hasNext()) {
            String building = iterator.next();
            if (building.equals(buildingToDestroy)) {
                iterator.remove();
                break;
            }
        }
        Map<String, Integer> sitesValue = new HashMap<>();

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                        sitesValue, buildings, null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    // Uses update island api-service to change the value of a system and add a building
    public Observable<SystemDto> createBuilding(String gameId, Island island, String buildingToAdd) {
        ArrayList<String> newBuildingsArray = island.buildings();
        newBuildingsArray.add(buildingToAdd);
        Map<String, Integer> sitesValue = new HashMap<>();

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameId, island.id(), new SystemsDto("",
                        sitesValue, newBuildingsArray,null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameId, island.id(), new SystemsDto("",
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    // Uses update island api-service to change the value of a system and delete a site
    public Observable<SystemDto> destroySite(String gameID, Island island, String siteToDestroy) {
        Map<String, Integer> sitesValue = new HashMap<>();
        sitesValue.put(siteToDestroy, -1);


        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                        sitesValue, island.buildings(), null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    // Uses update island api-service to change the value of a system and add a site
    public Observable<SystemDto> buildSite(String gameID, Island island, String siteToBuild) {
        Map<String, Integer> sitesValue = new HashMap<>();
        sitesValue.put(siteToBuild, 1);

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                        sitesValue, island.buildings(), null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id(), new SystemsDto("",
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }


    public Observable<SiteDto> getResourcesSite(String siteType) {
        return gameSystemsApiService.getSite(siteType);
    }

    public Observable<BuildingDto> getResourcesBuilding(String buildingType) {
        return gameSystemsApiService.getBuilding(buildingType);
    }

    /**
     * Updates the ObservableList which shows the count and change per season of a resource
     */
    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList, AggregateItemDto[] aggregateItems) {
        currentResources = resourceMap;
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : resourceMap.entrySet()) {
            String resourceID = entry.getKey();
            int count = entry.getValue();
            int changeProSeason = 0;
            if (!oldResourceList.isEmpty() && oldResourceList.size() >= 2) {
                changeProSeason = oldResourceList.get(i).changePerSeason();
            }
            if (Objects.nonNull(aggregateItems)) {
                changeProSeason = aggregateItems[i].subtotal();
            }
            Resource resource = new Resource(resourceID, count, changeProSeason);
            resourceList.add(resource);
            i++;
        }
        return resourceList;
    }

    public boolean hasEnoughResources(Map<String, Integer> neededResources) {
        for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
            String res = entry.getKey();
            int neededAmount = entry.getValue();
            int availableAmount = islandAttributes.getAvailableResources().get(res);
            if (availableAmount < neededAmount) {
                return false;
            }
        }
        return true;
    }

    public Map<String, Integer> updateAvailableResources(Map<String, Integer> neededResources) {
        Map<String, Integer> difRes = new HashMap<>();
        for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
            String res = entry.getKey();
            int neededAmount = entry.getValue();
            difRes.put(res, -neededAmount);
        }
        return difRes;
    }


    public void upgradeEmpire() {
        Map<String, Integer> difRes = updateAvailableResources(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()));

        this.subscriber.subscribe(empireService.updateEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId(),
                new UpdateEmpireDto(difRes, islandAttributes.getTech(), null, null, null)),
                result -> {
                    islandAttributes.setEmpireDto(result);
                });

    }
}
