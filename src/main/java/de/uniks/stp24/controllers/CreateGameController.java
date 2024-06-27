package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.menu.CreateGameService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.event.OnKey;
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
    GamesApiService gamesApiService;
    @Inject
    BrowseGameController browseGameController;
    @Inject
    @SubComponent
    BubbleComponent bubbleComponent;
    @FXML
    Pane captainContainer;

    private Timeline increaseTimeline;
    private Timeline decreaseTimeline;

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
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 200);
        valueFactory.setValue(100);
        createMapSizeSpinner.setEditable(false);
        createMapSizeSpinner.setValueFactory(valueFactory);

        createMapSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue < 50 || newValue > 200) {
                    createMapSizeSpinner.getValueFactory().setValue(oldValue);
                }
            }
        });
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
        maxMembersTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    maxMembersTextField.setText(newValue.replaceAll("\\D", ""));
                }
                try {
                    int value = Integer.parseInt(newValue);
                    if (value < 1 || value > 100) {
                        maxMembersTextField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    maxMembersTextField.setText("");
                }
            }
        });
    }

    @OnDestroy
    public void destroy(){
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }

    private void setupKeyPressHandlers() {
        createMapSizeSpinner.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                startIncreasing();
            } else if (event.getCode() == KeyCode.DOWN) {
                startDecreasing();
            }
        });

        createMapSizeSpinner.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.UP) {
                stopIncreasing();
            } else if (event.getCode() == KeyCode.DOWN) {
                stopDecreasing();
            }
        });
    }

    private void startIncreasing() {
        if (increaseTimeline != null && increaseTimeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }
        increaseTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> increaseSpinnerValue()));
        increaseTimeline.setCycleCount(Timeline.INDEFINITE);
        increaseTimeline.play();
    }

    private void stopIncreasing() {
        if (increaseTimeline != null) {
            increaseTimeline.stop();
        }
    }

    private void startDecreasing() {
        if (decreaseTimeline != null && decreaseTimeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }
        decreaseTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> decreaseSpinnerValue()));
        decreaseTimeline.setCycleCount(Timeline.INDEFINITE);
        decreaseTimeline.play();
    }

    private void stopDecreasing() {
        if (decreaseTimeline != null) {
            decreaseTimeline.stop();
        }
    }

    private void increaseSpinnerValue() {
        int currentValue = createMapSizeSpinner.getValue();
        if (currentValue < 200) {
            createMapSizeSpinner.getValueFactory().setValue(currentValue + 1);
        }
    }

    private void decreaseSpinnerValue() {
        int currentValue = createMapSizeSpinner.getValue();
        if (currentValue > 50) {
            createMapSizeSpinner.getValueFactory().setValue(currentValue - 1);
        }
    }
}

