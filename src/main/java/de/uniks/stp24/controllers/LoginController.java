package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Flow;

@Title("Login")
@Controller
public class LoginController {
    @FXML
    ToggleButton showPasswordToggleButton;
    @FXML
    Text errorLabel;
    @FXML
    CheckBox rememberMeBox;
    @FXML
    Button loginButton;
    @FXML
    Button signupButton;
    @FXML
    PasswordField passwordInput;
    @FXML
    TextField usernameInput;
    @FXML
    TextField showPasswordText;

    @Inject
    App app;
    @Inject
    Subscriber subscriber;

    @Inject
    LoginService loginService;

    @Param("username")
    public String username;
    @Param("password")
    public String password;

    @Inject
    public LoginController() {
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username))
            this.usernameInput.setText(this.username);
        if (Objects.nonNull(this.password))
            this.passwordInput.setText(this.password);
    }

    private boolean checkIfInputNotBlankOrEmpty(String text) {
        return (!text.isBlank() && !text.isEmpty());
    }

    public void login(ActionEvent actionEvent) {
        if (checkIfInputNotBlankOrEmpty(this.usernameInput.getText()) &&
                checkIfInputNotBlankOrEmpty(this.passwordInput.getText())) {
            this.errorLabel.setText("");
            String username = this.usernameInput.getText();
            String password = this.passwordInput.getText();
            boolean rememberMe = this.rememberMeBox.isSelected();
            //ToDo: button sperren wenn die Anfrage läuft
            subscriber.subscribe(loginService.login(username, password, rememberMe), result ->
                    app.show("/browseGames")
            );

        } else {
            this.errorLabel.setStyle("-fx-fill: red;");
            this.errorLabel.setText("please put in name or/and password");
        }
    }

    public void signup(ActionEvent actionEvent) {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        app.show("/signup", Map.of("username", username, "password", password));
    }

    public void setEn() {
    }

    public void setDe(ActionEvent actionEvent) {
    }

    @OnRender(1)
    public void setupShowPassword() {
        // TextField showPasswordText is per default not managed

        // setting properties managed and visible to change depending
        // showPasswordToggleButton state
        showPasswordText.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        showPasswordText.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        passwordInput.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());
        passwordInput.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());

        // binding textValue from both fields
        showPasswordText.textProperty().bindBidirectional(passwordInput.textProperty());

    }

    public void showLicenses(ActionEvent actionEvent) {
        app.show("/licenses");
    }
}
