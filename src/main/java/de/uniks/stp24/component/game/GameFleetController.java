package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.game.FleetCoordinationService;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;

    FleetCoordinationService fleetCoordinationService;
    public Fleet fleet;

    @Inject
    public GameFleetController(FleetCoordinationService fleetCoordinationService){
        this.fleetCoordinationService = fleetCoordinationService;
    };

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }

    public void setActive() {
        System.out.println("triggered!");
        this.activeCircle.setVisible(!this.activeCircle.isVisible());
        this.fleetCoordinationService.setFleet(this);
    }

}
