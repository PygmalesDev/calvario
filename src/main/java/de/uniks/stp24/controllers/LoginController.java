package de.uniks.stp24.controllers;

import de.uniks.stp24.App;

import de.uniks.stp24.utils.ResponseConstants;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.utils.ErrorTextWriter;
import de.uniks.stp24.service.LanguageService;
import de.uniks.stp24.service.LoginService;
import de.uniks.stp24.service.PrefService;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
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
public class LoginController {
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
    App app;
    @Inject
    LoginService loginService;
    @Inject
    ErrorService errorService;
    @Inject
    LanguageService languageService;
    @Inject
    PrefService prefService;
    @Inject
    @Resource
    ResourceBundle resources;
    @Inject
    ResponseConstants responseConstants;

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

                default -> this.errorLabel.setText("");
            }
        }
//        if (justRegistered){ this.errorLabel.setText(resources.getString("account.registered"));}
        if(prefService.getLocale() == Locale.ENGLISH) {
            enToggleButton.setSelected(true);
        } else {
            deToggleButton.setSelected(true);
        }
    }

    private boolean checkIfInputNotBlankOrEmpty(String text) {
        return (!text.isBlank() && !text.isEmpty());
    }

    public void login() {
        if (checkIfInputNotBlankOrEmpty(this.usernameInput.getText()) &&
                checkIfInputNotBlankOrEmpty(this.passwordInput.getText())) {
            this.errorLabel.setText("");
            String username = this.usernameInput.getText();
            String password = this.passwordInput.getText();
            boolean rememberMe = this.rememberMeBox.isSelected();

            loginButton.setDisable(true);
            signupButton.setDisable(true);
            this.errorLabel.setStyle("-fx-fill: black;");
            this.errorLabel.setText(resources
                    .getString(responseConstants.respLogin.get(201)));

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
                                .setText(resources
                                        .getString(new ErrorTextWriter(responseConstants.respLogin,code).getErrorText()));
                        enableButtons();
                    });
        } else {
            // 1 is used for default in switch
            this.errorLabel.setStyle("-fx-fill: red;");
            this.errorLabel.setText(resources
                    .getString(new ErrorTextWriter(responseConstants.respLogin,-1).getErrorText()));
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
