package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.WarningScreenComponent;
import de.uniks.stp24.model.User;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.TokenStorage;
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
    ToggleButton changeUserInfoButton;
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
    @Inject
    TokenStorage tokenStorage;

    @SubComponent
    @Inject
    WarningScreenComponent warningScreen;

    private BooleanBinding warningIsInvisible;

    @Inject
    public EditAccController() {
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(tokenStorage.getName()))
            this.usernameInput.setText(tokenStorage.getName());
    }

    @OnRender
    public void createBindings(){
        this.warningIsInvisible = this.warningScreenContainer.visibleProperty().not();
    }


    @OnRender
    public void setBlurEffect() {
        // blurs the edit account screen when the warning screen is visible
        this.editAccHBox.effectProperty().bind(Bindings.createObjectBinding(()->{
            if(warningIsInvisible.get())
                return null;
            return new BoxBlur();
        },this.warningIsInvisible));
    }

    @OnRender
    public void setWarningScreen(){
        // warning screen component is set but not visible
        warningScreenContainer.getChildren().add(warningScreen);
        warningScreenContainer.setVisible(false);
    }

    @OnRender
    public void changeUserInfo(ActionEvent actionEvent) {
        // If the changeUserButton is selected, username and password can be edited
        if(changeUserInfoButton.isSelected()){
            passwordInput.setDisable(false);
            usernameInput.setDisable(false);
            cancelChangesButton.setVisible(true);
            saveChangesButton.setVisible(true);
        }else{
            resetEditing(tokenStorage.getName());
        }
    }


    public void saveChanges(ActionEvent actionEvent) {
        // save changed name and/or password of the user and reset the edit account screen afterward
        subscriber.subscribe(editAccService.changeUserInfo(usernameInput.getText(), passwordInput.getText()),
                result -> {resetEditing(usernameInput.getText());
                    changeUserInfoButton.setSelected(false);});
        //ToDo: error handling and message
    }

    public void resetEditing(String username) {
        // Reset inputs and changeUserInfoButton
        usernameInput.setText(username);
        passwordInput.setText("");
        usernameInput.setDisable(true);
        passwordInput.setDisable(true);

        cancelChangesButton.setVisible(false);
        saveChangesButton.setVisible(false);

        changeUserInfoButton.setDisable(false);
    }

    public void cancelChanges(ActionEvent actionEvent) {
        // Reset inputs and changeUserInfoButton
        changeUserInfoButton.setSelected(false);
        resetEditing(tokenStorage.getName());
    }


    public void deleteUser(ActionEvent actionEvent) throws IOException {
        // warning screen opens
        warningScreenContainer.setVisible(true);
    }

    public void goBack(ActionEvent actionEvent) {
        app.show("/browseGames");
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
