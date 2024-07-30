package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.rest.FleetApiService;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.rest.ShipsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    @Inject
    public VariableDependencyService variableDependencyService;

    @Inject
    public ShipService(){}


    Map<String, ObservableList<Ship>> fleetShips = new HashMap<>();
    ObservableList<Ship> gameShips = FXCollections.observableArrayList();
    public ArrayList<ShipType> shipTypesAttributes;

    public void initShipTypes(){
        shipTypesAttributes = variableDependencyService.createVariableDependencyShipType();
        System.out.println(shipTypesAttributes);
    }

    public Map<String, Integer> getNeededResources(String type) {
        for(ShipType shipType : shipTypesAttributes){
            if (shipType._id().equals(type)) {
                return shipType.cost();
            }
        }
        return null;
    }

    //Todo: load all ships

    public void initializeShipListeners() {
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.ships.*.*", this.tokenStorage.getGameId()),
                Ship.class), event -> {
            Ship ship = event.data();
            switch (event.suffix()) {
                case "created" -> this.addShipToGroups(ship);
                case "updated" -> this.updateShipInGroups(ship);
                case "deleted" -> this.deleteShipFromGroups(ship);
            }
        });
    }

    private void deleteShipFromGroups(Ship ship) {
        this.gameShips.removeIf(other -> other.equals(ship));
        this.fleetShips.get(ship._id()).removeIf(other -> other.equals(ship));
    }

    private void updateShipInGroups(Ship ship) {
        this.gameShips.replaceAll(old -> old.equals(ship) ? ship : old);
        this.fleetShips.get(ship._id()).replaceAll(old -> old.equals(ship) ? ship : old);
    }

    private void addShipToGroups(Ship ship) {
       this.gameShips.add(ship);
       if(Objects.nonNull(ship.fleet())){
           if (!this.fleetShips.containsKey(ship.fleet())) {
               this.fleetShips.put(ship.fleet(), FXCollections.observableArrayList());
           }
           this.fleetShips.get(ship.fleet()).add(ship);
       }
    }

    public Observable<ReadShipDTO[]> getShipsOfFleet(String fleetID){
        return shipsApiService.getAllShips(tokenStorage.getGameId(), fleetID);
    }

    public Observable<Jobs.Job> beginShipJob(String fleetID, String shipType, String systemID) {
        return this.jobsApiService.createShipJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(),
                Jobs.createShipJob(fleetID, shipType, systemID));
    }

    public Observable<Ship> deleteShip(ReadShipDTO readShipDTO){
        return this.shipsApiService.deleteShip(this.tokenStorage.getGameId(),readShipDTO.fleet(), readShipDTO._id());
    }


}






















