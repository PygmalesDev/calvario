package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.CreateGameService;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;

@Title("CreateGame")
@Controller
public class CreateGameController {
    @FXML
    Text errorMessageText;
    @FXML
    HBox errorBox;
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
    BrowseGameController browseGameController;

    @Inject
    public CreateGameController(){

    }
    @Inject
    CreateGameService createGameService;

    @FXML
    public void initialize() {
        createGameService = (createGameService == null) ? new CreateGameService() : createGameService;
        createGameService.setCreateGameController(this);
        initializeSpinner();
    }

    //Spinner for incrementing map size
    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        createMapSizeSpinner.setValueFactory(valueFactory);
    }

    public void createGame() {
        if (!this.createNameTextField.getText().isEmpty() &&
                !this.createPasswordTextField.getText().isEmpty() &&
                this.createPasswordTextField.getText().equals(createRepeatPasswordTextField.getText()) &&
                this.createMapSizeSpinner.getValue() != null) {
            String gameName = this.createNameTextField.getText();
            String password = this.createPasswordTextField.getText();
            GameSettings settings = new GameSettings(this.createMapSizeSpinner.getValue());
            if (createGameService.createGame(gameName, settings, password) != null) {
                /*
                Platform run later makes sure updating the ui will be done on ui thread
                subscribeOn(Schedulers.io()) & subscribeOn(Schedulers.io()) makes sure
                that call of createGame is done on a different background thread so
                the ui is not blocked.
                 */
                createGameService.createGame(gameName, settings, password).subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.single())
                        .subscribe(result -> {
                            Platform.runLater(() -> {
                                browseGameController.init();
                                app.show(browseGameController);
                            });
                        });
            }
        }
    }

    public void cancel(){
        app.show("/browseGames");
    }

    public void showErrorBox() {
        errorBox.setVisible(true);
    }

    public void showNameTakenError() {
        errorMessageText.setText("Name exists already!");
        errorBox.setVisible(true);

    }

    public void hideErrorBox() {
        errorBox.setVisible(false);
    }

    public void setCreateGameService(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }
}
