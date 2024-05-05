package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.CreateGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;

@Title("CreateGame")
@Controller
public class CreateGameController {
    @FXML
    Button createGameConfirmButton;
    @FXML
    Button createGameCancelButton;
    @FXML
    TextField createNameTextField;
    @FXML
    TextField createPasswordTextField;
    @FXML
    TextField createRepeatPasswordTextField;
    @FXML
    Spinner<Integer> createMapSizeSpinner;

    @Inject
    App app;
    @Inject
    GamesApiService gamesApiService;

    @Inject
    public CreateGameController(){

    }
    @Inject
    CreateGameService createGameService;

    @FXML
    public void initialize() {
        initializeSpinner();
    }


    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        createMapSizeSpinner.setValueFactory(valueFactory);
        System.out.println(createMapSizeSpinner.getValue());
    }



    public void createGame(){
        if (!this.createNameTextField.getText().isEmpty() &&
                !this.createPasswordTextField.getText().isEmpty() &&
                this.createPasswordTextField.getText().equals(createRepeatPasswordTextField.getText()) &&
                this.createMapSizeSpinner.getValue() != null) {
            String gameName = this.createNameTextField.getText();
            String password = this.createPasswordTextField.getText();
            GameSettings settings = new GameSettings(this.createMapSizeSpinner.getValue());
            createGameService.createGame(gameName, settings, password).subscribe(result ->
                    System.out.println(result));
        }
    }
    public void cancel(){
        app.show("/browseGames");
    }
}
