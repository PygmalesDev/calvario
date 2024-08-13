package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.FleetService;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.List;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;

    private final FleetService fleetService;
    private final FleetCoordinationService fleetCoordinationService;
    private final Timeline travelTimeline = new Timeline();
    private final Fleet fleet;
    private final Rotate rotate = new Rotate();
    private boolean isTraveling = false;
    private int keyFrameTime = 0;

    @Inject
    public GameFleetController(Fleet fleet, FleetCoordinationService fleetCoordinationService, FleetService fleetService){
        this.fleet = fleet;
        this.fleetService = fleetService;
        this.fleetCoordinationService = fleetCoordinationService;
        this.getTransforms().add(rotate);
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

    public void beginTravelAnimation(List<Double[]> coordinates) {
        this.isTraveling = true;

        this.resetKeyFrameTime();
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();

        this.setRotate(this.calculateAngle(coordinates.getFirst()));
        this.travelTimeline.getKeyFrames().addAll(coordinates.stream().map(coord -> new KeyFrame(
                Duration.seconds(this.nextKeyFrameTime()),
                new KeyValue(this.layoutXProperty(), coord[0], Interpolator.EASE_BOTH),
                new KeyValue(this.layoutYProperty(), coord[1], Interpolator.EASE_BOTH)
        )).toList());

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
        return Math.atan2(toCoords[1]-this.getLayoutY(), toCoords[0]-this.getLayoutX()) * 180/Math.PI;
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

    public Fleet getFleet() {
        return fleet;
    }
}
