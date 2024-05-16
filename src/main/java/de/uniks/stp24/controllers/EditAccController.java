package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.component.WarningScreenComponent;
import de.uniks.stp24.model.ErrorResponse;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;
import retrofit2.HttpException;

import javax.inject.Inject;
import java.util.Objects;

@Title("Edit Account")
@Controller
public class EditAccController {
    @FXML
    ImageView avatarImage;
    @FXML
    Text errorLabelEditAcc;
    @FXML
    ToggleButton changeUserInfoButton;
    @FXML
    Button deleteUserButton;
    @FXML
    Button goBackButton;
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
    @Inject
    ImageCache imageCache;
    @Inject
    ObjectMapper objectMapper;


    @SubComponent
    @Inject
    WarningScreenComponent warningScreen;

    private BooleanBinding warningIsInvisible;
    private BooleanBinding editAccIsNotSelected;

    @Inject
    public EditAccController() {
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(tokenStorage.getName()))
            this.usernameInput.setText(tokenStorage.getName());
        this.avatarImage.setImage(imageCache.get(
                Objects.nonNull(tokenStorage.getAvatar()) ? tokenStorage.getAvatar() : "test/911.png" ));
        this.errorLabelEditAcc.setText("");

    }

    @OnRender
    public void createBindings(){
        this.warningIsInvisible = this.warningScreenContainer.visibleProperty().not();
        this.editAccIsNotSelected = this.changeUserInfoButton.selectedProperty().not();
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
    public void changeUserInfo() {
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

    @OnRender
    public void disableButtons(){
        this.deleteUserButton.disableProperty()
                .bind(Bindings.createBooleanBinding(()-> !editAccIsNotSelected.get(),
                        this.editAccIsNotSelected));

        this.goBackButton.disableProperty()
                .bind(Bindings.createBooleanBinding(()-> !editAccIsNotSelected.get(),
                        this.editAccIsNotSelected));
    }

    private boolean checkIfInputNotBlankOrEmpty(String text) {
        return (!text.isBlank() && !text.isEmpty());
    }

    public void saveChanges() {
        // save changed name and/or password of the user and reset the edit account screen afterward
        if (checkIfInputNotBlankOrEmpty(usernameInput.getText()) &&
        checkIfInputNotBlankOrEmpty(passwordInput.getText())) {
            this.errorLabelEditAcc.setText("");
            subscriber.subscribe(editAccService.changeUserInfo(usernameInput.getText(), passwordInput.getText()),
                    result -> {
                        resetEditing(usernameInput.getText());
                        changeUserInfoButton.setSelected(false);
                    }
                    // in case of server's response => error
                    // handle with error response
                    , error -> {
                        if (error instanceof HttpException httpError) {
                            System.out.println(httpError.code());
                            String body = httpError.response().errorBody().string();
                            ErrorResponse errorResponse = objectMapper.readValue(body,ErrorResponse.class);
                            writeText(errorResponse.statusCode());
                        }
                    });
        } else {
            writeText(1);

        }
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

    public void cancelChanges() {
        // Reset inputs and changeUserInfoButton
        this.errorLabelEditAcc.setText("");
        changeUserInfoButton.setSelected(false);
        resetEditing(tokenStorage.getName());
    }


    public void deleteUser() {
        // warning screen opens
        warningScreenContainer.setVisible(true);
    }

    public void goBack() {
        app.show("/browseGames");
    }

    // if response from server => error, choose a text depending on code
    private void writeText(int code) {
        this.errorLabelEditAcc.setStyle("-fx-fill: red;");
        String info;
        switch (code) {
            case 400 -> info = "invalid password";
            case 401 -> info = "validation failed";
            case 403 -> info = "attempting to change someone else's user";
            case 409 -> info = "username in use by another user";
            default ->  info = "please put in name or/and password";
        }
        this.errorLabelEditAcc.setText(info);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
