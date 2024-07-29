package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.utils.VectorMath;
import de.uniks.stp24.utils.VectorMath.Vector2D;
import javafx.animation.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static de.uniks.stp24.service.Constants.FLEET_HW;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;
    public Pane colorPane;

    private final FleetService fleetService;
    private final FleetCoordinationService fleetCoordinationService;
    private final Timeline travelTimeline = new Timeline();
    private final Fleet fleet;
    private final Rotate rotate = new Rotate();
    private final int TRAVEL_DURATION = 2;
    private boolean isTraveling = false;
    private int keyFrameTime = 0;

    @Inject
    public GameFleetController(Fleet fleet, FleetCoordinationService fleetCoordinationService, FleetService fleetService){
        this.fleet = fleet;
        this.fleetService = fleetService;
        this.fleetCoordinationService = fleetCoordinationService;
        this.getTransforms().add(rotate);
    }

    public void setEmpireColor(String color) {
        this.colorPane.setStyle(this.colorPane.getStyle().replace("white", color));
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
                new KeyValue(this.layoutXProperty(), mouseEvent.getX()- FLEET_HW, Interpolator.EASE_BOTH),
                new KeyValue(this.layoutYProperty(), mouseEvent.getY()- FLEET_HW, Interpolator.EASE_BOTH)
        ));

        this.travelTimeline.play();
    }

    public void beginTravelAnimation(List<Vector2D> coordinates) {
        this.isTraveling = true;

        this.resetKeyFrameTime();
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();

        this.setRotate(this.calculateAngle(coordinates.getFirst()));

        /*
         TODO: For now, travel duration is equal to the duration of a single season on the maximum tick speed
         Duration should be calculated as a product of the slowest ship's speed and current tick speed
         eg.: speed * {60, 30, 20}seconds.

         Find a way to connect the property change listener from timer service with this method,
         so that when the tick speed changes, the travel path will be recalculated based on the
         new timer speed.

         If the game is paused, the ship should stop!
       */

        this.travelTimeline.getKeyFrames().addAll(coordinates.stream().map(vector2D -> new KeyFrame(
                Duration.seconds(this.nextKeyFrameTime()),
                new KeyValue(this.layoutXProperty(), vector2D.x()-FLEET_HW, Interpolator.EASE_BOTH),
                new KeyValue(this.layoutYProperty(), vector2D.y()-FLEET_HW, Interpolator.EASE_BOTH)
        )).toList());

        this.travelTimeline.play();
    }

    private int nextKeyFrameTime() {
        int prevTime = this.keyFrameTime;
        this.keyFrameTime += TRAVEL_DURATION;
        return prevTime;
    }

    private void resetKeyFrameTime() {
        this.keyFrameTime = TRAVEL_DURATION;
    }

    private double calculateAngle(Vector2D toCoords) {
        Vector2D deltaVec = new Vector2D(toCoords).sub(this.getLayoutX(), this.getLayoutY());
//        return Math.asin(deltaVec.y()/deltaVec.length()) * 180.0/Math.PI + 45;
        return Math.atan2(deltaVec.y() -this.getLayoutY(), deltaVec.x()-this.getLayoutX()) * 180/Math.PI + 45;
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

    public Fleet getFleet() {
        return fleet;
    }
}
