package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.EditGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;
@Title("EditGame")
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
    @Inject
    EditGameService editGameService;
    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        editMapSizeSpinner.setValueFactory(valueFactory);
    }

    public void editGame(){
        String gameName = this.editNameTextField.getText();
        editGameService.editGame(editNameTextField)
    }
    public void cancel(){
        app.show("/browseGames");
    }
}
