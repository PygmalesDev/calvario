package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.fulib.fx.annotation.controller.Controller;

import javax.inject.Inject;

@Controller
public class EditGameController {
    @FXML
    Button editGameConfirmButton;
    @FXML
    Button editGameCancelButton;
    @FXML
    TextField editNameTextField;
    @FXML
    TextField editPasswordTextField;
    @FXML
    TextField editRepeatPasswordTextField;
    @FXML
    Spinner<Integer> editMapSizeSpinner;

    @Inject
    App app;

    @Inject
    public EditGameController(){

    }

    public void cancel(){
        app.show("/browseGames");
    }
}
