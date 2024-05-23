package de.uniks.stp24.controllers;

import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.CreateGameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;

@Title("Create Game")
@Controller
public class CreateGameController extends BasicController {
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

    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

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
        errorMessageText.setText("");
        errorBox.setVisible(true);
        this.controlResponses = responseConstants.respCreateGame;
    }

    //Spinner for incrementing map size
    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        createMapSizeSpinner.setValueFactory(valueFactory);
    }

    public void createGame() {
        String gameName = this.createNameTextField.getText();
        String password = this.createPasswordTextField.getText();
        GameSettings settings = new GameSettings(this.createMapSizeSpinner.getValue());
        boolean pwdMatch = (this.createPasswordTextField.getText().equals(createRepeatPasswordTextField.getText()));
        if (checkIt(gameName, password) &&
                pwdMatch &&
                this.createMapSizeSpinner.getValue() != null &&
            createGameService.isCreable(gameName)) {
            if (createGameService.createGame(gameName, settings, password) != null) {
                /*
                Platform run later makes sure updating the ui will be done on ui thread
                subscribeOn(Schedulers.io()) & subscribeOn(Schedulers.io()) makes sure
                that call of createGame is done on a different background thread so
                the ui is not blocked.
                 */
                /*createGameService.createGame(gameName, settings, password).subscribe(result -> {
                            Platform.runLater(() -> {
                                browseGameController.init();
                                app.show(browseGameController);
                            });
                        },
                                    error -> {
                                        int code = errorService.getStatus(error);
                                        errorMessageText.setText(getErrorInfoText(this.controlResponses,code));
                        });*/
                subscriber.subscribe(createGameService.createGame(gameName, settings, password),
                  result -> {
                      Platform.runLater(() -> {
                          browseGameController.init();
                          app.show(browseGameController);
                      });
                  },
                  error -> {
                      int code = errorService.getStatus(error);
                      errorMessageText.setText(getErrorInfoText(this.controlResponses,code));
                  });
            }
        } else {
            if (!createGameService.isCreable(gameName)) System.out.println("HALLO");
            errorMessageText.setText(getErrorInfoText(this.controlResponses,
                    !pwdMatch ? -2 : -1));
        }
    }

    public void cancel(){
        app.show("/browseGames");
    }

    public void showError(int code) {
        errorMessageText.setText(getErrorInfoText(this.controlResponses,code));
    }

    public void setCreateGameService(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    @OnDestroy
    public void destroy(){
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }
}

