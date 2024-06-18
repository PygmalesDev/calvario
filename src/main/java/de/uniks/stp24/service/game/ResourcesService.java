package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.BuildingDto;
import de.uniks.stp24.dto.SiteDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.SystemsDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResourcesService {
    @Inject
    GameSystemsApiService gameSystemsApiService;
    @Inject
    Subscriber subscriber;

    @Inject
    TokenStorage tokenStorage;


    @Inject
    public ResourcesService() {}

    public boolean hasEnoughResources(Map<Resource, Integer> resourceMap){
        return false;
    }

    public void upgradeIsland(){

    }

    public Observable<SystemDto> destroyBuilding(String gameID, Island island) {
        String[] buildings = new String[1];
        buildings[0] = "exchange";
        Map<String, Integer> sitesValue = new HashMap<>();
        System.out.println(gameID + " ### " + island);


        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto("",
                        sitesValue, buildings, null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto(island.name(),
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    public Observable<SystemDto> createBuilding(String gameId, Island island, String buildingToAdd) {
        System.out.println(gameId + " ### " + island);
        String[] newBuildingsArray = new String[island.buildings().length + 1];
        System.arraycopy(island.buildings(), 0, newBuildingsArray, 0, island.buildings().length);
        newBuildingsArray[newBuildingsArray.length - 1] = buildingToAdd;
        Map<String, Integer> sitesValue = new HashMap<>();
        System.out.println(Arrays.toString(newBuildingsArray));

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameId, island.id_(), new SystemsDto("",
                        sitesValue, newBuildingsArray,null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameId, island.id_(), new SystemsDto(island.name(),
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    public Observable<SystemDto> destroySite(String gameID, Island island, String siteToDestroy) {
        System.out.println(island.sites() + " ####################");
        Map<String, Integer> sitesValue = new HashMap<>();
        sitesValue.put(siteToDestroy, -1);

        System.out.println(gameID + " ### " + island);

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto("",
                        sitesValue, island.buildings(), null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto(island.name(),
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    public Observable<SystemDto> buildSite(String gameID, Island island, String siteToBuild) {
        Map<String, Integer> sitesValue = new HashMap<>();
        sitesValue.put(siteToBuild, 1);
        System.out.println(island.sites() + " aaaaaaaaaa");

        System.out.println(gameID + " ### " + island);

        if (island.owner() != null){
            if (island.owner().equals(tokenStorage.getEmpireId())){
                return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto("",
                        sitesValue, island.buildings(), null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto(island.name(),
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }


    public Observable<SiteDto> getResourcesSite(String siteType) {
        return gameSystemsApiService.getSite(siteType);
    }

    public Observable<BuildingDto> getResourcesBuilding(String buildingType) {
        return gameSystemsApiService.getBuilding(buildingType);
    }


    public ObservableList<Resource> generateResourceList(Map<String, Integer> resourceMap, ObservableList<Resource> oldResourceList){
        int i = 0;
        ObservableList<Resource> resourceList = FXCollections.observableArrayList();
        for(Map.Entry<String, Integer> entry : resourceMap.entrySet()){
            if(entry.getValue() != null) {
                String resourceID = entry.getKey();
                int count = entry.getValue();
                int changeProSeason = 0;
                if(oldResourceList != null && !oldResourceList.isEmpty() && i < oldResourceList.size()) {
                    changeProSeason = oldResourceList.get(i).changePerSeason();//Todo: getProSeason(name, count);
                }
                Resource resource = new Resource(resourceID, count, changeProSeason);
                resourceList.add(resource);
                i++;
            } else {
                assert oldResourceList != null;
                if (entry.getKey().equals(oldResourceList.get(i).resourceID())) {
                    i++;
                }
            }
        }
        return resourceList;
    }

}
