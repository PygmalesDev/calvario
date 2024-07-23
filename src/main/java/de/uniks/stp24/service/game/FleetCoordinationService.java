package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.Objects;
import java.util.Random;

import static de.uniks.stp24.model.Fleets.Fleet;


@Singleton
public class FleetCoordinationService {
    @Inject
    TokenStorage tokenStorage;
    @Inject
    FleetService fleetService;
    @Inject
    IslandsService islandsService;
    @Inject
    App app;

    private GameFleetController selectedFleet = null;
    private InGameController inGameController;
    private final Random random = new Random();

    private final double ISLAND_RADIUS_X = (double) Constants.ISLAND_WIDTH/2;
    private final double ISLAND_RADIUS_Y = ((double) Constants.ISLAND_HEIGHT/2);

    @Inject
    public FleetCoordinationService() {}

    public void setInitialFleetPosition() {
        this.fleetService.onFleetCreated(this::putFleetOnMap);
        this.random.setSeed(Integer.parseInt(tokenStorage.getGameId().substring(0, 4), 16));

    }

    public void setFleet(GameFleetController fleet) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.activeCircle.setVisible(false);
            if (this.selectedFleet.equals(fleet)) this.selectedFleet = null;
            else this.selectedFleet = fleet;
        } else this.selectedFleet = fleet;
    }

    public void putFleetOnMap(Fleet fleet) {
        var island = this.islandsService.getIslandComponent(fleet.location());
        var gameFleet = this.app.initAndRender(new GameFleetController(this));
        gameFleet.setFleet(fleet);
        this.inGameController.setFleetOnMap(gameFleet);
        double angle = (random.nextInt(360)-90)*Math.PI/180;
        gameFleet.setLayoutX(island.getLayoutX() + ISLAND_RADIUS_X + ISLAND_RADIUS_X*Math.cos(angle));
        gameFleet.setLayoutY(island.getLayoutY() + ISLAND_RADIUS_Y + ISLAND_RADIUS_X*Math.sin(angle));
        gameFleet.collisionCircle.setRadius(Constants.FLEET_COLLISION_RADIUS);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void teleportFleet(MouseEvent mouseEvent) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.setLayoutX(mouseEvent.getX()-30);
            this.selectedFleet.setLayoutY(mouseEvent.getY()-30);
        }
    }
}
