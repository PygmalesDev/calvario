package de.uniks.stp24.controllers;

import de.uniks.stp24.component.BubbleComponent;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Title("REGISTER")
@Controller
public class SignUpController extends BasicController {
  
    @FXML
    ToggleButton languageToggleButton;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;
    @FXML
    Text errorTextField;
    @FXML
    Pane captainContainer;
    @FXML
    public Button registerButton;
    @FXML
    TextField showPasswordText;
    @FXML
    TextField showRepeatPasswordText;
    @FXML
    ToggleButton showPasswordToggleButton;
    @FXML
    ToggleButton enToggleButton;
    @FXML
    ToggleButton deToggleButton;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;
    @FXML
    Image calvarioLogoRegister;

    @Param("username")
    public String username;
    @Param("password")
    public String password;


    @Inject
    SignUpService signUpService;
    @Inject
    Subscriber subscriber;
    @Inject
    UserApiService userApiService;

    @SubComponent
    @Inject
    BubbleComponent bubbleComponent;

    private BooleanBinding isLoginFieldEmpty;
    private BooleanBinding isPasswordFieldEmpty;
    private BooleanBinding isRepeatPasswordEmpty;
    private BooleanBinding passwordInputsMatch;
    private BooleanBinding isPasswordTooShort;

    @Inject
    public SignUpController() {}

    @OnInit
    public void init(){
        this.controlResponses = responseConstants.respSignup;
    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
        Platform.runLater(() -> {
            bubbleComponent.addChildren(errorTextField);
            bubbleComponent.setCaptainText("");
            if (!errorTextField.getText().equals("")) {
                bubbleComponent.setCaptainText("");
            }
        });
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
        if(prefService.getLocale() == Locale.ENGLISH) {
            enToggleButton.setSelected(true);
        } else {
            deToggleButton.setSelected(true);
        }
    }

    // Shows an error message when input fields are empty or password inputs do not match
    @OnRender
    public void showErrorMessage() {
        this.errorTextField.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (this.isLoginFieldEmpty.get())
                        return resources.getString("pirate.register.tell.name");
                    if (this.isPasswordFieldEmpty.get())
                        return resources.getString("pirate.general.enter.password");
                    if (this.isPasswordTooShort.get() && !this.isPasswordFieldEmpty.get())
                        return resources.getString("pirate.register.8characters");
                    if (this.isRepeatPasswordEmpty.get() && !this.isPasswordFieldEmpty.get())
                        return resources.getString("pirate.general.repeat.password");
                    if (!this.passwordInputsMatch.get() && !this.isPasswordFieldEmpty.get() && !this.isPasswordFieldEmpty.get())
                        return resources.getString("pirate.register.passwords.dont.match");
                    return resources.getString("pirate.register.possible");
                }, this.isLoginFieldEmpty, this.isPasswordFieldEmpty,
                this.isRepeatPasswordEmpty, this.passwordInputsMatch,
                this.isPasswordTooShort));
    }

    // Disables register button when input fields are empty or password inputs do not match
    // modified passwords do not need to match
    @OnRender
    public void disableRegisterButton() {
        this.registerButton.disableProperty()
                .bind(this.isLoginFieldEmpty
                        .or(this.isPasswordFieldEmpty)
                        .or(this.isRepeatPasswordEmpty)
                        .or(this.isPasswordTooShort)
                );
        if (registerButton.isDisabled()) {
            // TODO for MT: change the logic? for different cases
            // errorTextField.setText(resources.getString("info.register"));
        }
    }

    public void register() {
        // TODO @mtfmtf
        // When there is error with registering, showErrorMessage() does not work
        // You can try to call showErrorMessage() after you get the error from server but then the error from server is
        // not shown
        if (checkIt(this.usernameField.getText(),
                this.passwordField.getText(),
                this.repeatPasswordField.getText()) &&
                this.passwordInputsMatch.getValue()) {
                this.errorTextField.textProperty().unbind();
                this.errorTextField
                            .setText(getErrorInfoText(this.controlResponses, 201));
                bubbleComponent.setErrorMode(false);

            this.subscriber.subscribe(this.signUpService.register(this.getUsername(), this.getPassword()),
                    result -> this.app.show("/login",
                            Map.of(
                                    "username", this.getUsername(),
                                    "password", this.getPassword(),
                                    "info", "registered"
                            )),
                    error -> {
                        int code = errorService.getStatus(error);
                    // "generate"" the output in the english/german
                    // due binding, the TextField was not accessible here -> modified
                        this.errorTextField.textProperty().unbind();
                        errorTextField.setText(getErrorInfoText(this.controlResponses, code));
                        bubbleComponent.setErrorMode(true);
                    });
            } else {
            int code = this.passwordInputsMatch.not().getValue() ? -2 : -1;
            this.errorTextField.textProperty().unbind();
            errorTextField.setText(getErrorInfoText(this.controlResponses, code));
            bubbleComponent.setErrorMode(true);
        }
    }

    // Returns user to the login screen
    public void goBack() {
        app.show("/login",
                Map.of("username", this.getUsername(),
                        "password", this.getPassword()
        ));
    }

    @OnRender(1)
    public void setupShowPassword() {
        // TextField showPasswordText is per default not managed
        // setting properties managed and visible to change depending on
        // showPasswordToggleButton state

        //passwordField
        showPasswordText.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        showPasswordText.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        passwordField.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());
        passwordField.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());

        // binding textValue from both fields
        showPasswordText.textProperty().bindBidirectional(passwordField.textProperty());

        //repeatPasswordField
        showRepeatPasswordText.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        showRepeatPasswordText.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        repeatPasswordField.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());
        repeatPasswordField.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());

        // binding textValue from both fields
        showRepeatPasswordText.textProperty().bindBidirectional(repeatPasswordField.textProperty());

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
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
        calvarioLogoRegister = null;
    }

    @FXML
    public void setEn() {
        setLanguage(Locale.ENGLISH);
        enToggleButton.setSelected(true);
        deToggleButton.setSelected(false);
    }

    public void setDe() {
        setLanguage(Locale.GERMAN);
        enToggleButton.setSelected(false);
        deToggleButton.setSelected(true);
    }

    public void setLanguage(Locale locale) {
        resources = languageService.setLocale(locale);
        app.refresh();
    }
}
