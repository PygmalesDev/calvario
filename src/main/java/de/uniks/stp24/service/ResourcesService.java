package de.uniks.stp24.service;

import de.uniks.stp24.dto.SystemsDto;
import de.uniks.stp24.dto.SystemsResultDto;
import de.uniks.stp24.model.Building;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
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

    public Observable<SystemsResultDto> destroyBuilding(String gameID, Island island) {
        // Define the resources with their initial counts (which can be zero for required and produced resources)
        Resource iron = new Resource("Iron", 0);
        Resource steel = new Resource("Steel", 0);
        Resource coal = new Resource("Coal", 0);

        // Define the required resources with the required counts
        Map<Resource, Integer> required = Map.of(
                new Resource("Iron", 0), 100,
                new Resource("Coal", 0), 50
        );

        // Define the production resources with the produced counts
        Map<Resource, Integer> production = Map.of(
                new Resource("Steel", 0), 70
        );

        // Define the consumption resources with the consumed counts
        Map<Resource, Integer> consumption = Map.of(
                new Resource("Iron", 0), 30,
                new Resource("Coal", 0), 20
        );

        // Define the capacity and upgrade level
        int capacity = 200;
        int upgrade = 1;
        Building building = new Building("Mine", required, production, consumption, capacity, upgrade);
        String[] buildings = island.buildings();


        if (island.owner().equals(tokenStorage.getUserId())){
            return gameSystemsApiService.updateIsland(gameID, island.id_(), new SystemsDto(island.name(),
                    island.districts(), island.buildings(),island.upgrade(), island.owner()));
        }
        return null;

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
