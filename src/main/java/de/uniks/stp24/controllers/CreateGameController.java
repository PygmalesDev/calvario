package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.service.menu.CreateGameService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("%create.game")
@Controller
public class CreateGameController extends BasicController {
    @FXML
    TextField maxMembersTextField;
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
    BrowseGameController browseGameController;
    @Inject
    @SubComponent
    BubbleComponent bubbleComponent;
    @FXML
    Pane captainContainer;

    @Inject
    public CreateGameController(){

    }
    @Inject
    CreateGameService createGameService;

    @OnRender
    public void initialize() {
        createGameService = (createGameService == null) ? new CreateGameService() : createGameService;
        createGameService.setCreateGameController(this);
        initializeSpinner();
        initializeMaxMembersTextField();
        this.controlResponses = responseConstants.respCreateGame;
    }

    @OnRender
    public void setCaptain() {
        this.captainContainer.getChildren().add(this.bubbleComponent);
        this.bubbleComponent.setCaptainText(this.resources.getString("pirate.newGame"));
    }

    //Spinner for incrementing map size between 50 and 200
    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 200);
        createMapSizeSpinner.setValueFactory(valueFactory);
    }

    // class was modified! some code was refactored
    // now use subscriber
    @OnKey(code = KeyCode.ENTER)
    public void createGame() {
        String gameName = this.createNameTextField.getText();
        String password = this.createPasswordTextField.getText();
        int maxMembers = 0;
        if(!maxMembersTextField.getText().isEmpty()) {
            maxMembers = Integer.parseInt(maxMembersTextField.getText());
        }
        GameSettings settings = new GameSettings(this.createMapSizeSpinner.getValue());
        boolean pwdMatch = (this.createPasswordTextField.getText().equals(createRepeatPasswordTextField.getText()));
        if (checkIt(gameName, password) &&
          pwdMatch &&
          this.createMapSizeSpinner.getValue() != null &&
          createGameService.nameIsAvailable(gameName) && maxMembers > 0) {
            subscriber.subscribe(createGameService.createGame(gameName, settings, password, maxMembers),
              result -> {
                      browseGameController.init();
                      app.show(browseGameController);
              },
              error -> {
                  this.bubbleComponent.setErrorMode(true);
                  this.bubbleComponent.setCaptainText(getErrorInfoText(error));
              });

        } else if (!createGameService.nameIsAvailable(gameName)) {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(409)); }
        else {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(
              !pwdMatch ? -2 : -1));
        }
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void cancel(){
        app.show("/browseGames");
    }

    public void showError(Throwable error) {
        errorMessageText.setText(getErrorInfoText(error));
    }

    public void setCreateGameService(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void initializeMaxMembersTextField() {
        maxMembersTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[1-4]?")) {
                return change;
            }
            return null;
        }));
    }

    @OnDestroy
    public void destroy(){
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }
}

