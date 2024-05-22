package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.component.WarningScreenComponent;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Objects;
import java.util.ResourceBundle;

@Title("Edit Account")
@Controller
public class EditAccController extends BasicController {
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
    @FXML
    ImageView editIconImageView;
    @FXML
    ImageView deleteIconImageView;

    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

    @Inject
    EditAccService editAccService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ImageCache imageCache;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    @Resource
    ResourceBundle resources;

    @SubComponent
    @Inject
    WarningScreenComponent warningScreen;

    private BooleanBinding warningIsInvisible;
    private BooleanBinding editAccIsNotSelected;
    private Image editIconBlueImage = new Image(getClass().getResource("/de/uniks/stp24/icons/editBlue.png").toExternalForm());
    private Image editIconBlackImage = new Image(getClass().getResource("/de/uniks/stp24/icons/editBlack.png").toExternalForm());
    private Image deleteIconRedImage = new Image(getClass().getResource("/de/uniks/stp24/icons/deleteRed.png").toExternalForm());
    private Image deleteIconBlackImage = new Image(getClass().getResource("/de/uniks/stp24/icons/deleteBlack.png").toExternalForm());


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
        warningScreen.setWarning(resources.getString("warning.deleteAccount"));
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
            changeUserInfoButton.setStyle("-fx-text-fill: #2B78E4");
            editIconImageView.setImage(editIconBlueImage);
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

    public void saveChanges() {
        // save changed name and/or password of the user and reset the edit account screen afterward
        if (checkIt(usernameInput.getText(),passwordInput.getText())) {
            this.errorLabelEditAcc.setStyle("-fx-fill: black;");
            this.errorLabelEditAcc.setText("");
            subscriber.subscribe(editAccService.changeUserInfo(usernameInput.getText(), passwordInput.getText()),
                    result -> {
                        resetEditing(usernameInput.getText());
                        changeUserInfoButton.setSelected(false);
                    }
                    // in case of server's response => error
                    // handle with error response
                    , error -> {
                        this.errorLabelEditAcc.setStyle("-fx-fill: red;");
                        // find the code in the error response
                        int code = errorService.getStatus(error);
                        // "generate"" the output in the english/german
                        this.errorLabelEditAcc
                                .setText(getErrorInfoText(responseConstants.respEditAcc,code));
                    });
        } else {
            this.errorLabelEditAcc.setStyle("-fx-fill: red;");
            this.errorLabelEditAcc.setText(getErrorInfoText(responseConstants.respEditAcc,-1));

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
        editIconImageView.setImage(editIconBlackImage);
        changeUserInfoButton.setStyle("-fx-text-fill: Black");
    }

    public void cancelChanges() {
        // Reset inputs and changeUserInfoButton
        this.errorLabelEditAcc.setText("");
        changeUserInfoButton.setSelected(false);
        resetEditing(tokenStorage.getName());
    }

    @OnRender
    public void changeDeleteButtonView(){
        this.deleteUserButton.styleProperty().bind(Bindings.createStringBinding(()->{
            if(warningIsInvisible.get())
                return "-fx-text-fill: Black";
            return "-fx-text-fill: #CF2A27";
        },this.warningIsInvisible));

        this.deleteIconImageView.imageProperty().bind(Bindings.createObjectBinding(()->{
            if(warningIsInvisible.get())
                return deleteIconBlackImage;
            return deleteIconRedImage;
        },this.warningIsInvisible));
    }

    public void deleteUser() {
        // warning screen opens
        warningScreenContainer.setVisible(true);
    }

    public void goBack() {
        app.show("/browseGames");
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }
}
