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
    Button building8;
    @FXML
    Button building7;
    @FXML
    Button building6;
    @FXML
    Button building5;
    @FXML
    Button building4;
    @FXML
    Button building3;
    @FXML
    Button building2;
    @FXML
    Button building1;
    @FXML
    Button closeWindowButton;


    @Inject
    public BuildingsWindowComponent(){

    }

    public void onClose(){
        setVisible(false);
    }
}
