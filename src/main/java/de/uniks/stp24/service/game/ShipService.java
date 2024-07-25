package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.rest.FleetApiService;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.rest.ShipsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

import static de.uniks.stp24.model.Ships.*;

@Singleton
public class ShipService {
    @Inject
    public Subscriber subscriber;
    @Inject
    public EventListener eventListener;
    @Inject
    public ShipsApiService shipsApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public JobsApiService jobsApiService;


    Map<String, ObservableList<Ship>> allShipsMap = new HashMap<>();

//    public void loadShips(String fleetID) {
//        this.subscriber.subscribe(this.shipsApiService.getAllShips(this.tokenStorage.getGameId(), fleetID),
//                readShipDTOS -> readShipDTOS.stream().map(Ship::shipFromReadDTO).forEach(this::addShipsToGroups),
//                error -> System.out.println("Error loading ships of a fleet:\n" + error.getMessage()));
//    }
}






















