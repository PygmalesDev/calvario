package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Jobs;
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
    public FleetService() {}

    Map<String, ObservableList<Fleet>> empireFleets = new HashMap<>();
    Map<String, ObservableList<Fleet>> islandFleets = new HashMap<>();
    ObservableList<Fleet> gameFleets = FXCollections.observableArrayList();
    List<Consumer<Fleet>> fleetCreatedConsumer = new ArrayList<>();

    public void loadGameFleets() {
        this.subscriber.subscribe(this.fleetApiService.getGameFleets(this.tokenStorage.getGameId()),
                readFleetDTOS -> readFleetDTOS.stream().map(Fleets::fleetFromReadDTO).forEach(this::addFleetToGroups),
                error -> System.out.println("Error loading game fleets in the FleetService:\n" + error.getMessage()));
    }

    public void initializeFleetListeners() {
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.fleets.*.*", this.tokenStorage.getGameId()),
                Fleet.class), event -> {
            Fleet fleet = event.data();
            switch (event.suffix()) {
                case "created" -> this.addFleetToGroups(fleet);
                case "updated" -> this.updateFleetInGroups(fleet);
                case "deleted" -> this.deleteFleetFromGroups(fleet);
            }
        });
    }

    private void addFleetToGroups(Fleet fleet) {
        //Todo: remove print
        //System.out.println(fleet._id() + ": " + fleet.size() + " size of new added fleet");
        this.gameFleets.add(fleet);
        if (Objects.nonNull(fleet.empire())) {
            if (!this.empireFleets.containsKey(fleet.empire())) this.empireFleets.put(fleet.empire(), FXCollections.observableArrayList());
            this.empireFleets.get(fleet.empire()).add(fleet);
        }
        if (!this.islandFleets.containsKey(fleet.location())) this.islandFleets.put(fleet.location(), FXCollections.observableArrayList());
        this.islandFleets.get(fleet.location()).add(fleet);

        this.fleetCreatedConsumer.forEach(func -> func.accept(fleet));
    }

    private void updateFleetInGroups(Fleet fleet) {
        this.gameFleets.replaceAll(old -> old.equals(fleet) ? fleet : old);
        this.empireFleets.get(fleet.empire()).replaceAll(old -> old.equals(fleet) ? fleet : old);
        this.islandFleets.get(fleet.location()).replaceAll(old -> old.equals(fleet) ? fleet : old);
    }

    private void deleteFleetFromGroups(Fleet fleet) {
        this.gameFleets.removeIf(other -> other.equals(fleet));
        this.empireFleets.get(fleet.empire()).removeIf(other -> other.equals(fleet));
        this.islandFleets.get(fleet.location()).removeIf(other -> other.equals(fleet));
    }

    public Observable<Job> beginTravelJob(ArrayList<String> path, String fleetID) {
        return this.jobsApiService.createTravelJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(),
                Jobs.createTravelJob(path, fleetID));
    }

    public void onFleetCreated(Consumer<Fleet> func) {
        this.fleetCreatedConsumer.add(func);
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

    public Observable<Fleet> editSizeOfFleet(String shipTypeID, int count, Fleet fleet){
        Map<String, Integer> newSize = fleet.size();
        newSize.put(shipTypeID, count);
        return this.fleetApiService.patchFleet(this.tokenStorage.getGameId(),fleet._id(), new UpdateFleetDTO(fleet.name(), newSize, fleet._public(), fleet._private(), fleet.effects()));
    }

    public void dispose() {
        this.fleetCreatedConsumer.clear();
        this.subscriber.dispose();
        this.empireFleets.clear();
        this.islandFleets.clear();
        this.gameFleets.clear();
    }
}
