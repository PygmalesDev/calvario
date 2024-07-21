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
    public GameSystemsApiService gameSystemsApiService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public EmpireService empireService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;

    public ArrayList<Runnable> runnables = new ArrayList<>();

    /**
     * storage for actual resources
     */
    private Map<String, Integer> currentResources = new HashMap<>();

    @Inject
    public ResourcesService() {
    }

    public int getResourceCount(String resourceId) {
        if (currentResources.containsKey(resourceId))
            return currentResources.get(resourceId);
        return 0;
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

    public Observable<BuildingDto> getResourcesBuilding(String buildingType) {
        return gameSystemsApiService.getBuilding(buildingType);
    }

    // made a new method in order to prevent currentResources being overwritten in generateResourceList
    public void setCurrentResources(Map<String, Integer> resourceMap) {
        currentResources = resourceMap;
    }

    /**
     * Updates the ObservableList which shows the count and change per season of a resource
     */
    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList, AggregateItemDto[] aggregateItems) {
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        if (Objects.nonNull(resourceMap)) {
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
        }
        return resourceList;
    }

    public boolean hasEnoughResources(Map<String, Integer> neededResources) {
        if (currentResources.isEmpty()) {
            this.subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                    result -> {
                        islandAttributes.setEmpireDto(result);
                        currentResources = result.resources();
                    },
                    error -> System.out.println("error in getEmpire in inGame"));
        }

        if (Objects.nonNull(neededResources)) {
            for (Map.Entry<String, Integer> entry : neededResources.entrySet()) {
                String res = entry.getKey();
                int neededAmount = entry.getValue();
                int availableAmount = currentResources.get(res);
                if (availableAmount < neededAmount) {
                    return false;
                }
            }
        }

        return true;
    }

    public void setOnResourceUpdates(Runnable func) {
        // methods to run after resource updates
        runnables.add(func);
    }

    public Resource aggregateItemDtoToResource(AggregateItemDto aggregateItemDto) {
        String resourceID = aggregateItemDto.variable().replace("resources.", "").replace(".periodic", "");
        int resourceCount = getResourceCount(resourceID);
        return new Resource(resourceID, resourceCount, aggregateItemDto.subtotal());
    }
}
