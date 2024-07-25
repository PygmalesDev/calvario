package de.uniks.stp24.component.game.fleetManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

@Component(view = "ShipTypesOfFleet.fxml")
public class ShipTypesOfFleetComponent extends VBox {
    @FXML
    public Label typeLabel;
    @FXML
    public Label sizeLabel;

    public void buildShip(){}

    public void decrementSize(){}

    public void incrementSize(){}
}
