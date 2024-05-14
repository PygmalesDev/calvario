package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.SignUpService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

@Title("SignUp")
@Controller
public class SignUpController {
    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;
    @FXML
    Text errorTextField;
    @FXML
    public Button registerButton;

    @Param("username")
    public String username;
    @Param("password")
    public String password;

    @Inject
    App app;
    @Inject
    SignUpService signUpService;
    @Inject
    Subscriber subscriber;
    @Inject
    UserApiService userApiService;

    private BooleanBinding isLoginFieldEmpty;
    private BooleanBinding isPasswordFieldEmpty;
    private BooleanBinding isRepeatPasswordEmpty;
    private BooleanBinding passwordInputsMatch;
    private BooleanBinding isPasswordTooShort;

    @Inject
    public SignUpController() {
    }

    // Sets boolean bindings for text manipulations
    @OnRender
    public void createBindings() {
       this.isLoginFieldEmpty =  this.usernameField.textProperty().isEmpty();
       this.isPasswordFieldEmpty = this.passwordField.textProperty().isEmpty();
       this.isRepeatPasswordEmpty = this.repeatPasswordField.textProperty().isEmpty();
       this.isPasswordTooShort = this.passwordField.lengthProperty().lessThan(8);
       this.passwordInputsMatch = Bindings.equal(this.passwordField.textProperty(),
               this.repeatPasswordField.textProperty());
    }

    // Gets inputs from login screen to the signup screen fields
    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username))
            this.usernameField.setText(this.username);
        if (Objects.nonNull(this.password))
            this.passwordField.setText(this.password);
    }

    // Disables register button when input fields are empty or password inputs do not match
    @OnRender
    public void disableRegisterButton() {
        this.registerButton.disableProperty()
                .bind(this.isLoginFieldEmpty
                        .or(this.isPasswordFieldEmpty)
                        .or(this.isRepeatPasswordEmpty)
                        .or(this.passwordInputsMatch.not())
                        .or(this.isPasswordTooShort)
                );
    }

    // Shows an error message when input fields are empty or password inputs do not match
    @OnRender
    public void showErrorMessage() {
        this.errorTextField.textProperty().bind(Bindings.createStringBinding(() -> {
            if (this.isLoginFieldEmpty.get())
                return "Please enter a username";
            if (this.isPasswordFieldEmpty.get())
                return "Please enter a password";
            if (this.isPasswordTooShort.get())
                return "Password must contain at least 8 characters";
            if (this.isRepeatPasswordEmpty.get())
                return "Please repeat the password";
            if (!this.passwordInputsMatch.get())
                return "Passwords do not match";
            return "";
        }, this.isLoginFieldEmpty, this.isPasswordFieldEmpty,
                this.isRepeatPasswordEmpty, this.passwordInputsMatch,
                this.isPasswordTooShort));
    }

    public void register() {
        this.subscriber.subscribe(this.signUpService.register(this.getUsername(), this.getPassword()),
                result -> this.app.show("/login",
                        Map.of(
                                "username", this.getUsername(),
                                "password", this.getPassword(),
                                "justRegistered", true
                        )));
    }

    // Returns user to the login screen
    public void goBack() {
        app.show("/login",
                Map.of("username", this.getUsername(),
                        "password", this.getPassword()
        ));
    }

    private String getUsername() {
        return this.usernameField.getText();
    }

    private String getPassword() {
        return this.passwordField.getText();
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
