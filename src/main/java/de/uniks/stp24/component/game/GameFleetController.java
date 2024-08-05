package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.helper.DistancePoint;
import de.uniks.stp24.model.DistrictAttributes;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.utils.VectorMath;
import de.uniks.stp24.utils.VectorMath.Vector2D;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.awt.*;
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
    private DistancePoint currentPoint;
    private final Timeline travelTimeline = new Timeline();
    private final Rotate rotate = new Rotate();
    private final Fleet fleet;

    @Inject
    public GameFleetController(Fleet fleet, FleetCoordinationService fleetCoordinationService, FleetService fleetService){
        this.fleet = fleet;
        this.fleetService = fleetService;
        this.fleetCoordinationService = fleetCoordinationService;
    }

    public void setEmpireColor(String color) {
        this.colorPane.setStyle(this.colorPane.getStyle().replace("white", color));
    }

    public void setActive() {
        this.activeCircle.setVisible(!this.activeCircle.isVisible());
        this.fleetCoordinationService.setFleet(this);
        this.collisionCircle.setPickOnBounds(true);
    }

    public void travelToPoint(List<KeyFrame> keyFrame, DistancePoint currentPoint) {
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();
        this.currentPoint = currentPoint;

        this.travelTimeline.getKeyFrames().addAll(keyFrame);
        this.travelTimeline.play();
    }

    public void stopTravel() {
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();
    }

    public DistancePoint getCurrentPoint() {
        return this.currentPoint;
    }

    public DistancePoint getCurrentLocation() {
        return new DistancePoint(
                this.getLayoutX(),
                this.getLayoutY(),
                null
        );
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setStartingPoint() {
        this.colorPane.getTransforms().add(rotate);
    }

    public Rotate getFleetRotate() {
        return this.rotate;
    }
}
