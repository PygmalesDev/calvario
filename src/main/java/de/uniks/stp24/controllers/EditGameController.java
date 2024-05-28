package de.uniks.stp24.controllers;

import de.uniks.stp24.component.BubbleComponent;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import javax.inject.Inject;

@Title("%edit.game")
@Controller
public class EditGameController extends BasicController {
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
    @Inject
    public EditGameController() {
    }
    @OnRender
    public void initialize() {
        editGameService.setEditGameController(this);
        initializeSpinner();
        errorMessageTextEdit.setText("");
        errorBoxEdit.setVisible(true);
        this.controlResponses = responseConstants.respEditGame;
    }

    @OnRender
    public void setCaptain() {
        this.captainContainer.getChildren().add(this.bubbleComponent);
        this.bubbleComponent.setCaptainText("");
    }

    public void initializeSpinner(){
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 1000);
        valueFactory.setValue(100);
        editMapSizeSpinner.setEditable(true);
        editMapSizeSpinner.setValueFactory(valueFactory);
    }

// class was modified! some code was refactored
// now use subscriber

    public void editGame() {
        this.bubbleComponent.setErrorMode(false);
        GameSettings settings = new GameSettings(this.editMapSizeSpinner.getValue());
        String gameName = this.editNameTextField.getText();
        String password = this.editPasswordTextField.getText();
        boolean pwdMatch = password.equals(editRepeatPasswordTextField.getText());
        if (checkIt(gameName, password) &&
            pwdMatch &&
            editGameService.nameIsAvailable(gameName)) {
                subscriber.subscribe(editGameService.editGame(gameName, settings, password),
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
}
