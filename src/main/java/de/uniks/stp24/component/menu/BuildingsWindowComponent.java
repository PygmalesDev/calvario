package de.uniks.stp24.component.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "BuildingsWindow.fxml")
public class BuildingsWindowComponent extends AnchorPane {
    @FXML
    Button closeWindowButton;
    @FXML
    ImageView buildingImage8;
    @FXML
    ImageView buildingImage7;
    @FXML
    ImageView buildingImage6;
    @FXML
    ImageView buildingImage5;
    @FXML
    ImageView buildingImage4;
    @FXML
    ImageView buildingImage3;
    @FXML
    ImageView buildingImage2;
    @FXML
    ImageView buildingImage1;

    @Inject
    public BuildingsWindowComponent(){

    }

    public void onClose(){
        setVisible(false);
    }
}
