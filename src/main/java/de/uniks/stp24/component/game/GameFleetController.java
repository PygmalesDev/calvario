package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.game.FleetCoordinationService;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;

    FleetCoordinationService fleetCoordinationService;
    private Fleet fleet;
    private final Rotate rotate = new Rotate();

    @Inject
    public GameFleetController(FleetCoordinationService fleetCoordinationService){
        this.fleetCoordinationService = fleetCoordinationService;
        this.getTransforms().add(rotate);
    }

    public Rotate getRotation() {
        return this.rotate;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    public void setActive() {
        System.out.println("triggered!");
        this.activeCircle.setVisible(!this.activeCircle.isVisible());
        this.fleetCoordinationService.setFleet(this);
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

}
