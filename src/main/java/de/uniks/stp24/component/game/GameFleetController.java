package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.game.FleetCoordinationService;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.Map;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle activeCircle;
    public Circle collisionCircle;
    @FXML
    public ProgressBar healthBar;

    private double fleetHealth;

    FleetCoordinationService fleetCoordinationService;
    public Fleet fleet;

    @Inject
    public GameFleetController(FleetCoordinationService fleetCoordinationService){
        this.fleetCoordinationService = fleetCoordinationService;
    };

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
        if(!this.fleet.size().isEmpty()) setFleetHealth(this.fleet.size());
    }

    private void setFleetHealth(Map<String, Integer> fleetMap) {
        this.fleetHealth = fleetMap.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void setActive() {
        System.out.println("triggered!");
        this.activeCircle.setVisible(!this.activeCircle.isVisible());
        this.fleetCoordinationService.setFleet(this);
    }

    public Fleet getFleet() {
        return this.fleet;
    }

    public void showHealth() {
        System.out.println("your health : " + this.fleetHealth);
        this.healthBar.setVisible(this.activeCircle.visibleProperty().get());
    }


}
