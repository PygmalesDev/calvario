package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.animation.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;
import java.util.List;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;

    private final FleetService fleetService;
    private final IslandsService islandsService;
    private final FleetCoordinationService fleetCoordinationService;
    private final Timeline travelTimeline = new Timeline();
    private final Fleet fleet;
    private final Rotate rotate = new Rotate();
    private boolean isTraveling = false;
    private int keyFrameTime = 0;

    private final double ISLAND_RADIUS_X = Constants.ISLAND_WIDTH/2;
    private final double ISLAND_RADIUS_Y = Constants.ISLAND_HEIGHT/2;

    @Inject
    public GameFleetController(Fleet fleet, FleetCoordinationService fleetCoordinationService, FleetService fleetService, IslandsService islandsService){
        this.fleet = fleet;
        this.fleetService = fleetService;
        this.fleetCoordinationService = fleetCoordinationService;
        this.islandsService = islandsService;
        this.getTransforms().add(rotate);
    }

    @OnInit
    public void init(){
        travelTimeline.currentTimeProperty().addListener((observable, oldValue, newValue) -> fleetCoordinationService.inGameController.removeFog(false, null,
                new Circle(this.getLayoutX() + Constants.FLEET_HW/2 + 10, this.getLayoutY() + Constants.FLEET_HW/2 + 15, collisionCircle.getRadius()/2)
        ));
    }

    public Rotate getRotation() {
        return this.rotate;
    }

    public void setActive() {
        this.activeCircle.setVisible(!this.activeCircle.isVisible());
        this.fleetCoordinationService.setFleet(this);
        this.collisionCircle.setPickOnBounds(true);
    }

    public void beginTravelAnimation(MouseEvent mouseEvent) {
        this.isTraveling = true;
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();

        this.travelTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(4),
                new KeyValue(this.layoutXProperty(), mouseEvent.getX()-Constants.FLEET_HW, Interpolator.EASE_BOTH),
                new KeyValue(this.layoutYProperty(), mouseEvent.getY()-Constants.FLEET_HW, Interpolator.EASE_BOTH)
        ));

        this.travelTimeline.play();
    }

    public void beginTravelAnimation(List<String> path) {
        this.isTraveling = true;

        this.resetKeyFrameTime();
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();

//        this.setRotate(this.calculateAngle(coordinates.getFirst()));
        this.travelTimeline.getKeyFrames().addAll(path.stream().map(islandID -> {
                IslandComponent islandComponent = this.islandsService.getIslandComponent(islandID);
                return new KeyFrame(
                Duration.seconds(this.nextKeyFrameTime()),
                event -> fleetCoordinationService.inGameController.removeFogFromIsland(true, islandComponent),
                new KeyValue(this.layoutXProperty(), islandComponent.getPosX()+ISLAND_RADIUS_X, Interpolator.EASE_BOTH),
                new KeyValue(this.layoutYProperty(), islandComponent.getPosY()+ISLAND_RADIUS_Y, Interpolator.EASE_BOTH)
               );}
        ).toList());

        this.travelTimeline.play();
    }

    private int nextKeyFrameTime() {
        int prevTime = this.keyFrameTime;
        this.keyFrameTime += 4;
        return prevTime;
    }

    private void resetKeyFrameTime() {
        this.keyFrameTime = 4;
    }

    private double calculateAngle(Double[] toCoords) {
        double deltaY = toCoords[1] - this.getLayoutY();
        double deltaX = toCoords[0] - this.getLayoutX();
        return Math.asin(deltaY/Math.sqrt(deltaX*deltaX + deltaY*deltaY));
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

    public Fleet getFleet() {
        return fleet;
    }
}
