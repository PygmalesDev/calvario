package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.LoginDto;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;
import javax.swing.*;
import java.util.Map;
import java.util.Objects;

@Title("Login")
@Controller
public class LoginController {
    @FXML
    ImageView showPasswordToggleButton;
    @FXML
    Text errorLabel;
    @FXML
    CheckBox rememberMeBox;
    @FXML
    Button loginButton;
    @FXML
    Button signupButton;
    @FXML
    Button showPasswordButton1;
    @FXML
    PasswordField passwordInput;
    @FXML
    TextField usernameInput;

    @Inject
    App app;

    @Inject
    LoginService loginService;

    @Param("username")
    public String username;
    @Param("password")
    public String password;

    @Inject
    public LoginController(){
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username))
            this.usernameInput.setText(this.username);
        if (Objects.nonNull(this.password))
            this.passwordInput.setText(this.password);
    }

    public void login(ActionEvent actionEvent) {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        //ToDo: button sperren wenn die Anfrage läuft
        loginService.login(username, password)
                .subscribe(result ->{
                    app.show("/browseGames");
                });
    }

    public void signup(ActionEvent actionEvent) {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        app.show("/signup", Map.of("username",username,"password",password));
    }

    public void setEn() {
    }

    public void setDe(ActionEvent actionEvent) {
    }

    public void showPassword(ActionEvent actionEvent) {
    }


}
