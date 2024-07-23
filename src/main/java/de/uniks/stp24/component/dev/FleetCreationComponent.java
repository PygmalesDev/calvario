package de.uniks.stp24.component.dev;

import de.uniks.stp24.App;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

import java.util.Random;

import static de.uniks.stp24.model.Fleets.*;

@Component(view = "FleetCreation.fxml")
public class FleetCreationComponent extends Pane {
    public Button createFleetButton;
    public Button teleportFleetButton;
    public Button deleteFleetButton;
    public ListView<Fleet> fleetListView;

    private final Random random = new Random();

    private String islandID = "";

    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    FleetService fleetService;
    @Inject
    App app;

    @Inject
    public FleetCreationComponent() {
    }

    @OnRender
    public void render() {
        this.fleetListView.setCellFactory(list -> new FleetPointer());
    }

    public void setIsland(String islandID) {
        this.islandID = islandID;
        this.fleetListView.setItems(this.fleetService.getFleetsOnIsland(islandID));
    }

    public void createFleet() {
        this.subscriber.subscribe(this.fleetService.createFleet(this.tokenStorage.getGameId(),
                createRandomFleet(this.islandID)), result -> {},
                error -> System.out.println("Error while creating a new fleet in the FleetCreationComponent:\n" + error.getMessage()));
    }

    public void teleportFleet() {}

    public void deleteFleet() {}

    private static class FleetPointer extends ListCell<Fleet> {

        public FleetPointer() {
            super();

        }
    }
}
