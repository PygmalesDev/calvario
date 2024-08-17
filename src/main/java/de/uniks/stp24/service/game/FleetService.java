package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.rest.FleetApiService;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;

import static de.uniks.stp24.model.Fleets.*;
import static de.uniks.stp24.model.Ships.readShipDTOFromShip;

@Singleton
public class FleetService {
    @Inject
    public Subscriber subscriber;
    @Inject
    public EventListener eventListener;
    @Inject
    public FleetApiService fleetApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public JobsApiService jobsApiService;

    @Inject
    public FleetService() {
    }

    Map<String, ObservableList<Fleet>> empireFleets = new HashMap<>();
    Map<String, ObservableList<Fleet>> islandFleets = new HashMap<>();
    ObservableList<Fleet> gameFleets = FXCollections.observableArrayList();
    List<Consumer<Fleet>> fleetCreatedConsumers = new ArrayList<>();
    List<Consumer<Fleet>> fleetDestroyedConsumers = new ArrayList<>();
    List<Runnable> fleetLoadingFinishedConsumers = new ArrayList<>();
    List<Consumer<Fleet>> fleetLocationChangedConsumers = new ArrayList<>();
    private String lastShipUpdate = "";
    private String lastShipCreation = "";
    private String lastShipDeletion= "";
    private boolean loadingFinished = false;

    public void loadGameFleets() {
        this.subscriber.subscribe(this.fleetApiService.getGameFleets(this.tokenStorage.getGameId(), true),
                readFleetDTOS -> {
                    readFleetDTOS.stream().map(Fleets::fleetFromReadDTO).forEach(this::addFleetToGroups);
                    this.loadingFinished = true;
                    this.fleetLoadingFinishedConsumers.forEach(Runnable::run);
                },
                error -> System.out.println("Error loading game fleets in the FleetService:\n" + error.getMessage()));
    }

    public void initializeFleetListeners() {
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.fleets.*.*", this.tokenStorage.getGameId()),
                Fleet.class), event -> {
            Fleet fleet = event.data();
            switch (event.suffix()) {
                case "created" -> this.addFleetToGroups(fleet);
                case "updated" -> {
                    Fleet oldFleet = this.gameFleets.filtered(fleetDto -> fleetDto._id().equals(fleet._id())).getFirst();
                    this.updateFleetInGroups(oldFleet,
                            new Fleet(fleet.createdAt(), fleet.updatedAt(), fleet._id(), fleet.game(), fleet.empire(), fleet.name(),
                                    fleet.location(), oldFleet.ships(), fleet.size(), fleet._public(), fleet._private(), fleet.effects()));
                }
                case "deleted" -> this.deleteFleetFromGroups(fleet);
            }
        }, error -> System.out.println("Caught an error in the fleet listener in the FleetService:\n" + error.getMessage()));
    }

    public void initializeShipListener() {
        this.subscriber.subscribe(this.eventListener.listen("games." + this.tokenStorage.getGameId() + ".fleets.*.ships.*.*",
                Ship.class), event -> {
            ReadShipDTO ship = readShipDTOFromShip(event.data());
            switch (event.suffix()) {
                case "created" -> {
                    if (!ship._id().equals(this.lastShipCreation)) {
                        adaptShipCount(ship.fleet(), 1);
                        this.lastShipCreation = ship._id();
                    }
                }
                case "deleted" -> {
                    if (!ship._id().equals(this.lastShipDeletion) && gameFleets.contains(ship.fleet())) {
                        adaptShipCount(ship.fleet(), -1);
                        this.lastShipDeletion = ship._id();
                    }
                }
            }
        }, //error -> System.out.println("Error initializing shipListener in FleetService :\n" + error.getMessage()));
                Throwable::printStackTrace);
    }

    public void adaptShipCount(String fleetID, int increment) {
        Fleet oldFleet = this.gameFleets.filtered(fleet -> fleet._id().equals(fleetID)).getFirst();
        this.updateFleetInGroups(oldFleet,
                new Fleet(oldFleet.createdAt(), oldFleet.updatedAt(), oldFleet._id(), oldFleet.game(), oldFleet.empire(),
                        oldFleet.name(), oldFleet.location(), oldFleet.ships() + increment, oldFleet.size(), oldFleet._public(), oldFleet._private(), oldFleet.effects()));
    }

    private void addFleetToGroups(Fleet fleet) {
        this.gameFleets.add(fleet);
        if (Objects.nonNull(fleet.empire())) {
            if (!this.empireFleets.containsKey(fleet.empire())) this.empireFleets.put(fleet.empire(), FXCollections.observableArrayList());
            this.empireFleets.get(fleet.empire()).add(fleet);
        }
        if (!this.islandFleets.containsKey(fleet.location())) this.islandFleets.put(fleet.location(), FXCollections.observableArrayList());
        this.islandFleets.get(fleet.location()).add(fleet);

        if (this.loadingFinished) this.fleetCreatedConsumers.forEach(func -> func.accept(fleet));
    }

    private void updateFleetInGroups(Fleet oldFleet, Fleet fleet) {
        this.gameFleets.replaceAll(old -> old.equals(fleet) ? fleet : old);
        if (Objects.nonNull(fleet.empire()))
            this.empireFleets.get(fleet.empire()).replaceAll(old -> old.equals(fleet) ? fleet : old);

        if (Objects.nonNull(oldFleet) && !fleet.location().equals(oldFleet.location())) {
            this.islandFleets.get(oldFleet.location()).removeIf(old ->
                    old.equals(fleet) && !old.location().equals(fleet.location()));

            if (!this.islandFleets.containsKey(fleet.location()))
                this.islandFleets.put(fleet.location(), FXCollections.observableArrayList());
            this.islandFleets.get(fleet.location()).add(fleet);

            this.fleetLocationChangedConsumers.forEach(func -> func.accept(fleet));
        } else {
            this.islandFleets.get(fleet.location()).replaceAll(old -> old.equals(fleet) ? fleet : old);
        }

    }

    private void deleteFleetFromGroups(Fleet fleet) {
        this.gameFleets.removeIf(other -> other.equals(fleet));
        this.empireFleets.get(fleet.empire()).removeIf(other -> other.equals(fleet));
        this.islandFleets.get(fleet.location()).removeIf(other -> other.equals(fleet));

        this.fleetDestroyedConsumers.forEach(func -> func.accept(fleet));
    }

    public Observable<Job> beginTravelJob(ArrayList<String> path, String fleetID) {
        return this.jobsApiService.createTravelJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(),
                Jobs.createTravelJob(path, fleetID));
    }

    public void onFleetCreated(Consumer<Fleet> func) {
        this.fleetCreatedConsumers.add(func);
    }

    public void onFleetDestroyed(Consumer<Fleet> func) {
        this.fleetDestroyedConsumers.add(func);
    }

    public void onLoadingFinished(Runnable func) {
        this.fleetLoadingFinishedConsumers.add(func);
    }

    public void onFleetLocationChanged(Consumer<Fleet> func) {
        this.fleetLocationChangedConsumers.add(func);
    }

    public ObservableList<Fleet> getGameFleets() {
        return this.gameFleets;
    }

    public Observable<Fleet> createFleet(String gameId, CreateFleetDTO fleet) {
        return this.fleetApiService.createFleet(gameId, fleet);
    }

    public Observable<Fleet> deleteFleet(String gameId, String fleetID) {
        return this.fleetApiService.deleteFleet(gameId, fleetID);
    }

    public ObservableList<Fleet> getFleetsOnIsland(String islandID) {
        if (!this.islandFleets.containsKey(islandID)) this.islandFleets.put(islandID, FXCollections.observableArrayList());
        return this.islandFleets.get(islandID);
    }

    public ObservableList<Fleet> getEmpireFleets(String empireID) {
        if (!this.empireFleets.containsKey(empireID)) this.empireFleets.put(empireID, FXCollections.observableArrayList());
        return this.empireFleets.get(empireID);
    }

    public Fleet getFleet(String fleetID){
        return this.gameFleets.filtered(fleet -> fleet._id().equals(fleetID)).getFirst();
    }

    public Observable<Fleet> editSizeOfFleet(String shipTypeID, int plannedShips, Fleet fleet){
        Map<String, Integer> newSize = fleet.size();
        newSize.put(shipTypeID, plannedShips);
        return this.fleetApiService.patchFleet(this.tokenStorage.getGameId(),fleet._id(),
                new UpdateFleetDTO(fleet.name(), newSize, fleet._public(), fleet._private(), fleet.effects()));
    }

    public void dispose() {
        this.fleetCreatedConsumers.clear();
        this.fleetLoadingFinishedConsumers.clear();
        this.fleetDestroyedConsumers.clear();
        this.fleetLocationChangedConsumers.clear();
        this.loadingFinished = false;
        this.subscriber.dispose();
        this.empireFleets.clear();
        this.islandFleets.clear();
        this.gameFleets.clear();
    }
}
