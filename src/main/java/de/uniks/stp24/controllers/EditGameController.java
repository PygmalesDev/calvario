package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.EditGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Title("EditGame")
@Controller
public class EditGameController {
    @FXML
    HBox errorBoxEdit;
    @FXML
    Text errorMessageTextEdit;
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
    GamesApiService gamesApiService;

    @Inject
    public EditGameController(){

    }
    @Inject
    EditGameService editGameService;
    @FXML
    public void initialize() {
        editGameService.setEditGameController(this);
        initializeSpinner();
    }

    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        editMapSizeSpinner.setValueFactory(valueFactory);
    }

    public void editGame(){
        initializeSpinner();
        GameSettings settings = new GameSettings(this.editMapSizeSpinner.getValue());
        String gameName = this.editNameTextField.getText();
        String password = this.editPasswordTextField.getText();
        if(!gameName.isEmpty() && !password.isEmpty() && !editRepeatPasswordTextField.getText().isEmpty()) {
            if(password.equals(editRepeatPasswordTextField.getText())) {
                if (editGameService.editGame(gameName, settings, password) != null) {
                    editGameService.editGame(gameName, settings, password)
                            .subscribe(System.out::println);
                    app.show("/browseGames");
                }
            }
        }

    }
    public void cancel(){
        app.show("/browseGames");
    }
    public void showNameTakenError() {
        errorMessageTextEdit.setText("Name exists already!");
        errorBoxEdit.setVisible(true);
    }

    public void hideErrorBox() {
        errorBoxEdit.setVisible(false);
    }
}
