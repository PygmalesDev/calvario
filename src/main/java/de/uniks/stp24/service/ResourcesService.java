package de.uniks.stp24.service;

import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.SystemsDto;
import de.uniks.stp24.dto.SystemsResultDto;
import de.uniks.stp24.dto.Upgrade;
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
                System.out.println("--------------");
                return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto("",
                        sitesValue, buildings, null, island.owner()));
            }
        }

        return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto(island.name(),
                island.sites(), island.buildings(),island.upgrade(), island.owner()));
    }

    public Observable<SystemDto> createBuilding(String gameId, Island island, String buildingToAdd) {
        String[] newBuildingsArray = new String[island.buildings().length + 1];
        System.arraycopy(island.buildings(), 0, newBuildingsArray, 0, island.buildings().length);
        newBuildingsArray[newBuildingsArray.length - 1] = buildingToAdd;
        Map<String, Integer> sitesValue = new HashMap<>();
        System.out.println(Arrays.toString(island.buildings()) + "###");
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

    /*
    Click logik:
    1.  Ich muss ein Click auf eine Insel simulieren ***
    2.  Nach diesem Klick, soll sich IslandOverviewSites öffnen
            -> Click auf UPGRADES öffnet IslandOverviewUpgrades
                -> Click auf Back zurück zu IslandOverviewSites
                -> Click auf x zurück zum Spiel
                -> Click auf Upgrade :  1. Fall nicht genug Resourcen > garnichts
                                        2. Fall genug Resourcen > Leuchtet Grün und upgrade Insel

            -> Click auf DETAILS öffnet IslandOverviewDetails (Nur untere Häflte ändert sich)
                -> Zeig Details untere Hälfte
                -> Click auf Sites zeigt IslandOverviewSites
                -> Click auf UPGRADES zeigt IslandOverviewUpgrades

     Simulieren einer Insel:
     1. Sitesinfos: Namen, Capacity, Crews, Tressure, (Settled), Besonderheit
     2. ListView Inf. for Details
     3. Informations for Sites
     4. Upgrade Informations
        -> Resources

     What we need:
     1. Test class
     2. Controller
     3. Services ?

     Ab wo starten ?
     Ab Loadgame. Dabei ist egal wo drauf geclickt wird.
     Sobald irgendwo draufgeklickt wird, werden alle Informationen der Insel dem neuen Controller und dem Service übergeben,
     sodass damit gearbeitet werden kann.

     Wie soll alles enden ?
     Endet mit Upgrade. Informationen der Insel werden erneuert.

     */

}
