package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.rest.FleetApiService;
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
import java.util.List;
import java.util.Map;
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
    public FleetService() {
    }

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
        this.gameFleets.add(fleet);
        if (!this.empireFleets.containsKey(fleet._id())) this.empireFleets.put(fleet._id(), FXCollections.observableArrayList());
        this.empireFleets.get(fleet._id()).add(fleet);
        if (!this.islandFleets.containsKey(fleet.location())) this.islandFleets.put(fleet.location(), FXCollections.observableArrayList());
        this.islandFleets.get(fleet.location()).add(fleet);

        this.fleetCreatedConsumer.forEach(func -> func.accept(fleet));
    }

    private void updateFleetInGroups(Fleet fleet) {
        this.gameFleets.replaceAll(old -> old.equals(fleet) ? fleet : old);
        this.empireFleets.get(fleet._id()).replaceAll(old -> old.equals(fleet) ? fleet : old);
        this.islandFleets.get(fleet.location()).replaceAll(old -> old.equals(fleet) ? fleet : old);
    }

    private void deleteFleetFromGroups(Fleet fleet) {
        this.gameFleets.removeIf(other -> other.equals(fleet));
        this.empireFleets.get(fleet._id()).removeIf(other -> other.equals(fleet));
        this.islandFleets.get(fleet.location()).removeIf(other -> other.equals(fleet));
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
        if (!this.islandFleets.containsKey(islandID)) this.empireFleets.put(islandID, FXCollections.observableArrayList());
        return this.islandFleets.get(islandID);
    }

    public void dispose() {
        this.fleetCreatedConsumer.clear();
        this.subscriber.dispose();
        this.empireFleets.clear();
        this.islandFleets.clear();
        this.gameFleets.clear();
    }
}
