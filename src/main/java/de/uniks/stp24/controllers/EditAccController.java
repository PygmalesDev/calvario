package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.WarningScreenComponent;
import de.uniks.stp24.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnRender;
import javafx.beans.binding.BooleanBinding;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

@Title("Edit Account")
@Controller
public class EditAccController {
    @FXML
    Button changeUserInfoButton;
    @FXML
    Button cancelChangesButton;
    @FXML
    Button saveChangesButton;
    @FXML
    TextField passwordInput;
    @FXML
    TextField usernameInput;
    @FXML
    StackPane warningScreenContainer;
    @FXML
    HBox editAccHBox;

    @SubComponent
    @Inject
    WarningScreenComponent warningScreen;



    @Inject
    App app;


    private BooleanBinding passwordInputChanged;
    private BooleanBinding usernameInputChanged;

    //@Param("user")
    public User user = new User("a","b","c","d","e");


    @Inject
    public EditAccController() {
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(user.name()))
            this.usernameInput.setText(user.name());
        // Todo: write password of the user
    }

    @OnRender
    public void createBindings(){
        this.passwordInputChanged = this.passwordInput.textProperty().isNotEqualTo("");
        this.usernameInputChanged = this.usernameInput.textProperty().isNotEqualTo(user.name());
    }

    @OnRender
    public void showChangingButtons(){
        // Show save and cancel changes Buttons, if the input has changed
        this.cancelChangesButton.visibleProperty().bind(this.passwordInputChanged.or(this.usernameInputChanged));
        this.saveChangesButton.visibleProperty().bind(this.passwordInputChanged.or(this.usernameInputChanged));
    }

    @OnRender
    public void addWarningScreen(){
        warningScreenContainer.setVisible(false);
        warningScreenContainer.getChildren().add(warningScreen);
    }

    public void saveChanges(ActionEvent actionEvent) {
        editAccHBox.setEffect(new BoxBlur());
        warningScreenContainer.setVisible(true);
    }

    public void cancelChanges(ActionEvent actionEvent) {
        // Reset inputs
        this.usernameInput.setText(user.name());
        this.passwordInput.setText("");
    }

    public void changeUserInfo(ActionEvent actionEvent) {
        // TextFields can be edited now
        passwordInput.setDisable(false);
        usernameInput.setDisable(false);
        changeUserInfoButton.setStyle("-fx-background-color: #00f0f0; ");
    }

    public void deleteUser(ActionEvent actionEvent) throws IOException {

    }

    public void goBack(ActionEvent actionEvent) {
        app.show("/browseGames");
    }
}
