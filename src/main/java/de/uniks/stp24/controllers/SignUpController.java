package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.CreateUserDto;
import de.uniks.stp24.service.SignUpService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import de.uniks.stp24.rest.UserApiService;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

@Controller
public class SignUpController {
    public TextField usernameField;
    public TextField passwordField;
    public TextField errorText;
    public Button registerButton;

    @Param("username")
    String username;
    @Param("password")
    String password;

    @Inject
    App app;
    @Inject
    SignUpService signUpService;
    @Inject
    UserApiService userApiService;

    private final BooleanBinding isLoginFieldEmpty =  this.usernameField.textProperty().isEmpty();
    private final BooleanBinding isPasswordFieldEmpty = this.passwordField.textProperty().isEmpty();


    public SignUpController() {
    }

    // Gets inputs from login screen to the signup screen fields.
    @OnRender
    private void applyInputs() {
        if (Objects.nonNull(this.username))
            this.usernameField.setText(this.username);
        if (Objects.nonNull(this.password))
            this.passwordField.setText(this.password);
    }

    // Disables register button when input fields are empty or password inputs do not match.
    @OnRender
    private void disableRegisterButton() {
        this.registerButton.setDisable(true);
        this.registerButton.disableProperty().bind(
                this.isLoginFieldEmpty.and(this.isPasswordFieldEmpty));
        // TODO: After the second password field is added, check for the equality of password inputs!
    }

    // Shows an error message when input fields are empty or password inputs do not match.
    @OnRender
    private void showErrorMessage() {
        this.errorText.textProperty().bind(Bindings.createStringBinding(() -> {
            if (this.isLoginFieldEmpty.get()) {
                return "Please enter a username";
            }
            if (this.isPasswordFieldEmpty.get()) {
                return "Please enter a password";
            }
            return "";
        }, this.isLoginFieldEmpty, this.isPasswordFieldEmpty));
    }

    private void register() {
        this.signUpService.register(this.getUsername(), this.getPassword());
    }

    // Returns user to the login screen.
    private void goBack() {
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
}
