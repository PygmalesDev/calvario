package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.component.game.IslandComponent;
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
    public TokenStorage tokenStorage;
    @Inject
    public FleetService fleetService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public App app;
    @Inject
    public ContactsService contactsService;

    private GameFleetController selectedFleet = null;
    public InGameController inGameController;
    public final Random random = new Random();

    private final double ISLAND_RADIUS_X = (double) Constants.ISLAND_WIDTH / 2;
    private final double ISLAND_RADIUS_Y = ((double) Constants.ISLAND_HEIGHT / 2);

    @Inject
    public FleetCoordinationService() {
    }

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
        if (!fleet.getFleet().size().isEmpty()) {
            System.out.println("your fleet has these ships: " + fleet.getFleet().size() );
            fleet.showHealth();
        }
    }

    public void putFleetOnMap(Fleet fleet) {
        var island = this.islandsService.getIslandComponent(fleet.location());
        var gameFleet = this.app.initAndRender(new GameFleetController(this));
        gameFleet.setFleet(fleet);
        this.inGameController.setFleetOnMap(gameFleet);
        double angle = (random.nextInt(360) - 90) * Math.PI / 180;
        gameFleet.setLayoutX(island.getLayoutX() + ISLAND_RADIUS_X + ISLAND_RADIUS_X * Math.cos(angle));
        gameFleet.setLayoutY(island.getLayoutY() + ISLAND_RADIUS_Y + ISLAND_RADIUS_X * Math.sin(angle));
        gameFleet.collisionCircle.setRadius(Constants.FLEET_COLLISION_RADIUS);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void teleportFleet(MouseEvent mouseEvent) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.setLayoutX(mouseEvent.getX() - 30);
            this.selectedFleet.setLayoutY(mouseEvent.getY() - 30);
            monitorFleetCollisions();
        }
    }

    public void monitorFleetCollisions() {
        for (IslandComponent islandComponent : inGameController.islandComponentList) {
            if (islandComponent.isCollided(selectedFleet.getLayoutX(), selectedFleet.getLayoutY(), Constants.FLEET_COLLISION_RADIUS)) {
                System.out.println("------------------------------------------COLLISION-------------------------------------- ");
                System.out.println("island: " + islandComponent.getIsland().id() + " at "
                  + islandComponent.getPosX() + ", " + islandComponent.getPosY());
                System.out.println("islandOwnerID" + islandComponent.getIsland().owner());
                System.out.println("fleetOwnerID" + selectedFleet.fleet.empire());

                if(Objects.nonNull(islandComponent.getIsland().owner())){
                    if (!islandComponent.getIsland().owner().equals(selectedFleet.fleet.empire()) && !islandComponent.getIsland().owner().equals(this.tokenStorage.getEmpireId())){
                        System.out.println("Enemy  detected");
                        // maybe remove this since refresh will occur after every tick
                        islandsService.refreshListOfColonizedSystems();
                        contactsService.addEnemy(islandComponent.getIsland().owner(), islandComponent.getIsland().id());
//                        contactsService.addEnemyAfterCollision(islandComponent.getIsland().owner());
                    }
                }
            }
        }
    }
}
