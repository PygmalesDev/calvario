package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnRender;
import javafx.beans.binding.BooleanBinding;
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Title("Edit Account")
@Controller
public class EditAccController {
    @FXML
    Button cancelChangesButton;
    @FXML
    Button saveChangesButton;
    @FXML
    TextField passwordInput;
    @FXML
    TextField usernameInput;

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
        this.cancelChangesButton.visibleProperty().bind(this.passwordInputChanged.or(this.usernameInputChanged));
        this.saveChangesButton.visibleProperty().bind(this.passwordInputChanged.or(this.usernameInputChanged));
    }

    public void saveChanges(ActionEvent actionEvent) {

    }

    public void cancelChanges(ActionEvent actionEvent) {
        this.usernameInput.setText(user.name());
        this.passwordInput.setText("");
    }

    public void continueGame(ActionEvent actionEvent) {
    }

    public void changeUserInfo(ActionEvent actionEvent) {
        passwordInput.setDisable(false);
        usernameInput.setDisable(false);
    }

    public void deleteUser(ActionEvent actionEvent) throws IOException {

    }

    public void goBack(ActionEvent actionEvent) {
        app.show("/browseGames");
    }
}
