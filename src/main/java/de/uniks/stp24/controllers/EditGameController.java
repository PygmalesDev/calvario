package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.EditGameService;
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

@Title("%edit.game")
@Controller
public class EditGameController extends BasicController {
    @FXML
    TextField maxMembersTextField;
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
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

    @Inject
    GamesApiService gamesApiService;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    BrowseGameController browseGameController;
    @Inject
    @SubComponent
    BubbleComponent bubbleComponent;
    @FXML
    Pane captainContainer;

    @Inject
    EditGameService editGameService;
    private Timeline increaseTimeline;
    private Timeline decreaseTimeline;
    @Inject
    public EditGameController() {
    }
    @OnRender
    public void initialize() {
        editGameService.setEditGameController(this);
        initializeSpinner();
        errorMessageTextEdit.setText("");
        errorBoxEdit.setVisible(true);
        initializeMaxMembersTextField();
        setupKeyPressHandlers();
        this.controlResponses = responseConstants.respEditGame;
    }

    @OnRender
    public void setCaptain() {
        this.captainContainer.getChildren().add(this.bubbleComponent);
        this.bubbleComponent.setCaptainText("");
    }

    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 200);
        valueFactory.setValue(100);
        editMapSizeSpinner.setEditable(false);
        editMapSizeSpinner.setValueFactory(valueFactory);

        editMapSizeSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue < 50 || newValue > 200) {
                    editMapSizeSpinner.getValueFactory().setValue(oldValue);
                }
            }
        });
    }

// class was modified! some code was refactored
// now use subscriber
    @OnKey(code = KeyCode.ENTER)
    public void editGame() {
        this.bubbleComponent.setErrorMode(false);
        int maxMembers = 0;
        if(!maxMembersTextField.getText().isEmpty()) {
            maxMembers = Integer.parseInt(maxMembersTextField.getText());
        }
        GameSettings settings = new GameSettings(this.editMapSizeSpinner.getValue());
        String gameName = this.editNameTextField.getText();
        String password = this.editPasswordTextField.getText();
        boolean pwdMatch = password.equals(editRepeatPasswordTextField.getText());
        if (checkIt(gameName, password) &&
            pwdMatch &&
            editGameService.nameIsAvailable(gameName) && maxMembers > 0) {
                subscriber.subscribe(editGameService.editGame(gameName, settings, password, maxMembers),
                  result -> {
                      browseGameController.init();
                      app.show(browseGameController);
                  },
                   error -> {
                      this.bubbleComponent.setErrorMode(true);
                      this.bubbleComponent.setCaptainText(getErrorInfoText(error));
});
        } else if(!editGameService.nameIsAvailable(gameName)) {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(409));
        } else {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(!pwdMatch ? -2 : -1));
        }
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void cancel(){
        browseGameService.resetSelectedGame();
        app.show("/browseGames");
    }
    public void showError(int code) {
        errorMessageTextEdit.setText(getErrorInfoText(code));
    }

    public void setEditGameService(EditGameService newEditGameService) {
        editGameService = newEditGameService;
    }

    @OnDestroy
    public void destroy(){
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }

    /*
    This method makes sure that on int values are accepted in text field
     */
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

    private void setupKeyPressHandlers() {
        editMapSizeSpinner.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.UP) {
                startIncreasing();
            } else if (event.getCode() == KeyCode.DOWN) {
                startDecreasing();
            }
        });

        editMapSizeSpinner.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.UP) {
                stopIncreasing();
            } else if (event.getCode() == KeyCode.DOWN) {
                stopDecreasing();
            }
        });
    }

    /*
    Creating timelines for increasing spinner value in a periodic way. That makes sure u can stop
    increasing after value increased by one.
     */
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
        int currentValue = editMapSizeSpinner.getValue();
        if (currentValue < 200) {
            editMapSizeSpinner.getValueFactory().setValue(currentValue + 1);
        }
    }

    private void decreaseSpinnerValue() {
        int currentValue = editMapSizeSpinner.getValue();
        if (currentValue > 50) {
            editMapSizeSpinner.getValueFactory().setValue(currentValue - 1);
        }
    }
}
