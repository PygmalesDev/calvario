package de.uniks.stp24.component.game.fleetManager;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "Ship.fxml")
public class ShipComponent extends VBox {
    @FXML
    public Label blueprintTypeLabel;
    @FXML
    public ImageView healthImage;
    @FXML
    public Label healthLabel;
    @FXML
    public ImageView speedImage;
    @FXML
    public Label speedLabel;
    @FXML
    public ImageView attackImage;
    @FXML
    public Label attackLabel;
    @FXML
    public ImageView defenseImage;
    @FXML
    public Label defenseLabel;


    @Inject
    public ShipComponent(){}

    public void changeFleet(){}

    public void deleteShip(){}
}
