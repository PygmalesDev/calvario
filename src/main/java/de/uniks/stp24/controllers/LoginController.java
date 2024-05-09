package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.model.ErrorResponse;
import de.uniks.stp24.service.LoginService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

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
    Subscriber subscriber;

    @Inject
    App app;

    @Inject
    LoginService loginService;

    @Inject
    ObjectMapper objectMappper;

    @Param("username")
    public String username;
    @Param("password")
    public String password;
    @Param("justRegistered")
    public boolean justRegistered;

    @Inject
    public LoginController(){
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username))
            this.usernameInput.setText(this.username);
        if (Objects.nonNull(this.password))
            this.passwordInput.setText(this.password);
        if (justRegistered){ this.errorLabel.setText("Account Registered!");}
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
            //ToDo: disable button during request
            loginButton.setDisable(true);
            subscriber.subscribe(loginService.login(username, password, rememberMe),
                    result ->{
                        app.show("/editAcc");
                    }
                    , error -> {
                                if (error instanceof HttpException httpError) {
                                    System.out.println(httpError.code());
                                    String body = httpError.response().errorBody().string();
                                    ErrorResponse errorResponse = objectMappper.readValue(body,ErrorResponse.class);
                                    writeText(errorResponse.statusCode());
                                }
                    });
        } else {
            writeText(1);
        }
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

    private void writeText(int code) {
        this.errorLabel.setStyle("-fx-fill: red;");
        String info;
        switch (code) {
            case 400 -> info = "validation failed";
            case 401 -> info = "Invalid username or password";
            default ->  info = "please put in name or/and password";
        }
        this.errorLabel.setText(info);
        loginButton.setDisable(false);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
