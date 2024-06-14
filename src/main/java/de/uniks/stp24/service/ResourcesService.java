package de.uniks.stp24.service;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Building;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.Site;
import de.uniks.stp24.rest.GameSystemsApiService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
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
    public ResourcesService() {

    }

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
}
