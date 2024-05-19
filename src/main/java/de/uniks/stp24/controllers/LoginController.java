package de.uniks.stp24.controllers;

import de.uniks.stp24.service.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


@Title("%login")
@Controller
public class LoginController extends BasicController {
    @FXML
    Button licensesButton;
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
    @FXML
    ToggleButton enToggleButton;
    @FXML
    ToggleButton deToggleButton;

    @Inject
    Subscriber subscriber;
    @Inject
    LoginService loginService;

    @Param("info")
    public String info;
    @Param("username")
    public String username;
    @Param("password")
    public String password;

    @Inject
    public LoginController() {
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username)) {
            this.usernameInput.setText(this.username);
        }
        if (Objects.nonNull(this.password)) {
            this.passwordInput.setText(this.password);
        }
        if (Objects.nonNull(this.info)) {
            switch(this.info) {
                case "logout" -> this.errorLabel
                        .setText(resources.getString("logout.successful.on.this.device"));

                case "registered" -> this.errorLabel
                        .setText(resources.getString("account.registered"));

                case "deleted" -> this.errorLabel
                        .setText(resources.getString("account.deleted"));

                default -> this.errorLabel.setText("");
            }
        }
        if(prefService.getLocale() == Locale.ENGLISH) {
            enToggleButton.setSelected(true);
        } else {
            deToggleButton.setSelected(true);
        }
    }

    public void login() {
        if (checkIt(this.usernameInput.getText()) &&
                checkIt(this.passwordInput.getText())) {
            this.errorLabel.setText("");
            String username = this.usernameInput.getText();
            String password = this.passwordInput.getText();
            boolean rememberMe = this.rememberMeBox.isSelected();

            loginButton.setDisable(true);
            signupButton.setDisable(true);
            this.errorLabel.setStyle("-fx-fill: black;");
            this.errorLabel.setText(getErrorInfoText(responseConstants.respLogin,201));

            subscriber.subscribe(loginService.login(username, password, rememberMe),
                    result -> app.show("/browseGames")
                    // in case of server's response => error
                    // handle with error response
                    , error -> {
                        this.errorLabel.setStyle("-fx-fill: red;");
                        // find the code in the error response
                        int code = errorService.getStatus(error);
                        // "generate"" the output in the english/german
                        this.errorLabel
                                .setText(getErrorInfoText(responseConstants.respLogin,code));
                        enableButtons();
                    });
        } else {
            // 1 is used for default in switch
            this.errorLabel.setStyle("-fx-fill: red;");
            this.errorLabel.setText(getErrorInfoText(responseConstants.respLogin,-1));
            enableButtons();
        }
    }

    public void signup() {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        app.show("/signup", Map.of("username", username, "password", password));
    }

    @FXML
    public void setEn() {
        setLanguage(Locale.ENGLISH);
        enToggleButton.setSelected(true);
        deToggleButton.setSelected(false);
    }

    @FXML
    public void setDe() {
        setLanguage(Locale.GERMAN);
        enToggleButton.setSelected(false);
        deToggleButton.setSelected(true);
    }

    public void setLanguage(Locale locale) {
        resources = languageService.setLocale(locale);
        app.refresh();
    }


    @OnRender(1)
    public void setupShowPassword() {
        // TextField showPasswordText is per default not managed

        // setting properties managed and visible to change depending on
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

    public void showLicenses() {
        app.show("/licenses");
    }

    public void enableButtons(){
        loginButton.setDisable(false);
        signupButton.setDisable(false);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
