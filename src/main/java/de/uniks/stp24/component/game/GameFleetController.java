package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
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
import java.util.Objects;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;


    private final FleetService fleetService;
    private final FleetCoordinationService fleetCoordinationService;
    private final Timeline travelAnimation = new Timeline();
    private final Fleet fleet;
    private final Rotate rotate = new Rotate();
    private boolean isTraveling = false;

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


    public void travelTo(IslandComponent island) {

    }

    public void beginTravelAnimation(MouseEvent mouseEvent) {
        this.isTraveling = true;
        this.travelAnimation.stop();
        this.travelAnimation.getKeyFrames().clear();

        this.travelAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(4),
                new KeyValue(this.layoutXProperty(), mouseEvent.getX()-30, Interpolator.EASE_BOTH),
                new KeyValue(this.layoutYProperty(), mouseEvent.getY()-30, Interpolator.EASE_BOTH)
        ));

        this.travelAnimation.play();
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

    public Fleet getFleet() {
        return fleet;
    }
}
