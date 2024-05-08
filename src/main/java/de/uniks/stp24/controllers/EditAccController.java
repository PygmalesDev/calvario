package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.WarningScreenComponent;
import de.uniks.stp24.model.User;
import de.uniks.stp24.service.EditAccService;
import javafx.beans.binding.Bindings;
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
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import javafx.beans.binding.BooleanBinding;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;
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

    @Inject
    EditAccService editAccService;
    @Inject
    Subscriber subscriber;
    @Inject
    App app;

    @SubComponent
    @Inject
    WarningScreenComponent warningScreen;





    private BooleanBinding warningIsVisible;

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
        this.warningIsVisible = this.warningScreenContainer.visibleProperty().not();
    }



    @OnRender
    public void setBlurEffect() {
        // blurs the edit account screen when the warning screen is visible
        this.editAccHBox.effectProperty().bind(Bindings.createObjectBinding(()->{
            if(warningIsVisible.get())
                return null;
            return new BoxBlur();
        },this.warningIsVisible));
    }

    @OnRender
    public void setWarningScreen(){
        // warning screen component is set but not visible
        warningScreenContainer.getChildren().add(warningScreen);
        warningScreenContainer.setVisible(false);
    }

    public void saveChanges(ActionEvent actionEvent) {
        // save changed name and/or password of the user and reset the edit account screen afterward
        subscriber.subscribe(editAccService.changeUserInfo(usernameInput.getText(), passwordInput.getText()),
                result -> resetEditing(usernameInput.getText(), passwordInput.getText()));
        //ToDo: error handling and message
    }

    public void resetEditing(String username, String password) {
        // Reset inputs and changeUserInfoButton
        usernameInput.setText(username);
        passwordInput.setText(password);
        usernameInput.setDisable(true);
        passwordInput.setDisable(true);

        cancelChangesButton.setVisible(false);
        saveChangesButton.setVisible(false);

        changeUserInfoButton.setStyle("-fx-background-color: #ffffff; ");
        changeUserInfoButton.setDisable(false);
    }

    public void cancelChanges(ActionEvent actionEvent) {
        // Reset inputs and changeUserInfoButton
        resetEditing(user.name(), "");
    }

    public void changeUserInfo(ActionEvent actionEvent) {
        // TextFields can be edited now and buttons for saving or cancel the changes show up
        passwordInput.setDisable(false);
        usernameInput.setDisable(false);

        cancelChangesButton.setVisible(true);
        saveChangesButton.setVisible(true);

        changeUserInfoButton.setStyle("-fx-background-color: #00f0f0; ");
        changeUserInfoButton.setDisable(true);
    }

    public void deleteUser(ActionEvent actionEvent) throws IOException {
        // warning screen opens
        warningScreenContainer.setVisible(true);
        // Todo: color of the deleteUserButton
    }

    public void goBack(ActionEvent actionEvent) {
        app.show("/browseGames");
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
