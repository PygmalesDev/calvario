package de.uniks.stp24.component.game.fleetManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

@Component(view = "Fleet.fxml")
public class FleetComponent extends VBox {
    @FXML
    public Label fleetNameLabel;
    @FXML
    public ImageView fleetImageview;
    @FXML
    public Label sizeLabel;


    public void deleteFleet(){}

    public void editFleet(){
    }
}
