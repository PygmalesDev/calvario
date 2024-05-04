package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.CreateGameService;
import de.uniks.stp24.service.EditGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnInit;

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
    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        createMapSizeSpinner.setValueFactory(valueFactory);
    }


    public void createGame(){
        //initializeSpinner();
        //int mapSize = this.createMapSizeSpinner.getValue();
        //String gameName = this.createNameTextField.getText();
        //String password = this.createPasswordTextField.getText();
        createGameService.createGame("gameName", 100, "password").subscribe(result ->
                System.out.println(result));
    }
    public void cancel(){
        app.show("/browseGames");
    }
}
