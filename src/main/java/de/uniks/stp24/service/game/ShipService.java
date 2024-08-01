package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Jobs;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static de.uniks.stp24.model.Fleets.*;
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


    ObservableList<ReadShipDTO> shipsInSelectedFleet = FXCollections.observableArrayList();
    public Map<String, Integer> blueprintsInFleetMap = new HashMap<>();
    public ObservableList<BlueprintInFleetDto> blueprintsInFleetList = FXCollections.observableArrayList();
    public ArrayList<ShipType> shipTypesAttributes;

    public void initShipTypes(){
        shipTypesAttributes = variableDependencyService.createVariableDependencyShipType();
    }

    public Map<String, Integer> getNeededResources(String type) {
        for(ShipType shipType : shipTypesAttributes){
            if (shipType._id().equals(type)) {
                return shipType.cost();
            }
        }
        return null;
    }

    public void initializeFleetEdition(ReadShipDTO[] dto, Fleet fleet){
        Arrays.stream(dto).forEach(ship -> {
            this.shipsInSelectedFleet.add(ship);
            if (!this.blueprintsInFleetMap.containsKey(ship.type())) {
                this.blueprintsInFleetMap.put(ship.type(), 1);
            } else {
                this.blueprintsInFleetMap.compute(ship.type(), (k, currentCount) -> currentCount + 1);
            }
        });
        fleet.size().forEach((key, value) -> blueprintsInFleetMap.putIfAbsent(key, 0));
        this.blueprintsInFleetList.addAll(this.blueprintsInFleetMap.entrySet().stream().map(entry ->
                new BlueprintInFleetDto(entry.getKey(), entry.getValue(), fleet)).toList());
        initializeShipListeners(fleet._id());
    }

    public ObservableList<ReadShipDTO> getShipsInSelectedFleet(){
        return this.shipsInSelectedFleet;
    }

    public ObservableList<BlueprintInFleetDto> getBlueprintsInSelectedFleet(){
        return this.blueprintsInFleetList;
    }

    public void clearEditedFleetInfos(){
        this.blueprintsInFleetMap.clear();
        this.shipsInSelectedFleet.clear();
        this.blueprintsInFleetList.clear();
        this.subscriber.dispose();
    }

    public void initializeShipListeners(String fleetID) {
        this.subscriber.subscribe(this.eventListener.listen("games." + this.tokenStorage.getGameId() + ".fleets." + fleetID + ".ships.*.*",
                Ship.class), event -> {
            ReadShipDTO ship = readShipDTOFromShip(event.data());
            switch (event.suffix()) {
                case "created" -> this.addShipToGroups(ship);
                case "updated" -> this.updateShipInGroups(ship);
                case "deleted" -> this.deleteShipFromGroups(ship);
            }
        }, error-> System.out.println("Error initializing shipListener in ShipService :\n" + error.getMessage()));
    }

    public void deleteShipFromGroups(ReadShipDTO ship) {
        this.shipsInSelectedFleet.removeIf(other -> other.equals(ship));
        replaceBlueprintByType(ship.type(), -1);
    }

    private void updateShipInGroups(ReadShipDTO ship) {
        this.shipsInSelectedFleet.replaceAll(old -> old.equals(ship) ? ship : old);
    }

    private void addShipToGroups(ReadShipDTO ship) {
        this.shipsInSelectedFleet.add(ship);
        replaceBlueprintByType(ship.type(), 1);
    }

    public void replaceBlueprintByType(String type, int diff) {
        for (int i = 0; i < blueprintsInFleetList.size(); i++) {
            BlueprintInFleetDto blueprint = blueprintsInFleetList.get(i);
            if (blueprint.type().equals(type)) {
                blueprintsInFleetList.set(i, new BlueprintInFleetDto(blueprint.type(), blueprint.count() + diff, blueprint.fleet()));
            }
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

    public Observable<Ship> changeFleetOfShip(String newFleetID, ReadShipDTO readShipDTO){
        return this.shipsApiService.patchShip(this.tokenStorage.getGameId(), readShipDTO.fleet(), readShipDTO._id(), new UpdateShipDTO(newFleetID, readShipDTO._public(), new HashMap<>()));
    }

}






















