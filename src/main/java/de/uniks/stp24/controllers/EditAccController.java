package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.WarningScreenComponent;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.PopupBuilder;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import javax.inject.Inject;
import java.util.Objects;

@Title("%edit.account")
@Controller
public class EditAccController extends BasicController {
    @FXML
    VBox editAccVBoxLeftToBlur;
    @FXML
    VBox editAccVBoxRightToBlur;
    @FXML
    Pane captainContainer;
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
    ObjectMapper objectMapper;
    @Inject
    PopupBuilder popupBuilder;
    @SubComponent
    @Inject
    BubbleComponent bubbleComponent;
    @SubComponent
    @Inject
    public WarningScreenComponent warningScreen;
    private BooleanBinding editAccIsNotSelected;
    private BooleanBinding warningIsInvisible;
    public Image editIconBlueImage;
    public Image editIconBlackImage;
    public Image deleteIconRedImage;
    public Image deleteIconBlackImage;
    PopupBuilder popup = new PopupBuilder();

    @Inject
    public EditAccController() {
    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
          bubbleComponent
            .setCaptainText(resources.getString("pirate.editAcc.go.into.hiding"));
    }

    @OnInit
    public void init(){
        editIconBlueImage = imageCache.get("icons/editBlue.png");
        editIconBlackImage = imageCache.get("icons/editBlack.png");
        deleteIconRedImage = imageCache.get("icons/deleteRed.png");
        deleteIconBlackImage = imageCache.get("icons/deleteBlack.png");
        this.controlResponses = responseConstants.respEditAcc;
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
        this.editAccIsNotSelected = this.changeUserInfoButton.selectedProperty().not();
        this.warningIsInvisible = this.warningScreenContainer.visibleProperty().not();
    }

    @OnRender
    public void changeUserInfo() {
        // If the changeUserButton is selected, username and password can be edited
        if(changeUserInfoButton.isSelected()){
            passwordInput.setEditable(true);
            usernameInput.setEditable(true);
            cancelChangesButton.setVisible(true);
            saveChangesButton.setVisible(true);
            changeUserInfoButton.setStyle("-fx-text-fill: #2B78E4");
            editIconImageView.setImage(editIconBlueImage);
        } else {
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

    @OnRender
    public void changeDeleteButtonView(){
        // delete Button has red text and icon when selected
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

    public void saveChanges() {
        // save changed name and/or password of the user and reset the edit account screen afterward
        if (checkIt(usernameInput.getText(),passwordInput.getText())) {
            this.errorLabelEditAcc.setText("");
            this.bubbleComponent.setErrorMode(false);
            subscriber.subscribe(editAccService.changeUserInfo(usernameInput.getText(), passwordInput.getText()),
                result -> {
                    resetEditing(usernameInput.getText());
                    changeUserInfoButton.setSelected(false);
                }
                // in case of server's response => error
                // handle with error response
                , error -> {
                    this.bubbleComponent.setErrorMode(true);
                    this.bubbleComponent.setCaptainText(getErrorInfoText(error));
                });
        } else {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(passwordInput.getLength() > 8 ? -1 : -2 ));
        }
    }

    public void resetEditing(String username) {
        // Reset inputs and changeUserInfoButton
        usernameInput.setText(username);
        passwordInput.setText("");
        usernameInput.setEditable(false);
        passwordInput.setEditable(false);
        cancelChangesButton.setVisible(false);
        saveChangesButton.setVisible(false);
        changeUserInfoButton.setDisable(false);
        editIconImageView.setImage(editIconBlackImage);
        changeUserInfoButton.setStyle("-fx-text-fill: Black");
    }

    public void cancelChanges() {
        // Reset inputs and changeUserInfoButton
        this.bubbleComponent.setErrorMode(false);
        this.bubbleComponent.setCaptainText(resources.getString("pirate.editAcc.go.into.hiding"));
        changeUserInfoButton.setSelected(false);
        resetEditing(tokenStorage.getName());
    }

    public void deleteUser() {
        // warning screen opens
        popup.showPopup(warningScreenContainer, warningScreen);
        popup.setBlur(editAccVBoxLeftToBlur, editAccVBoxRightToBlur);
        warningScreen.setWarning(resources.getString("warning.deleteAccount") + tokenStorage.getName() + ".");
    }

    public void goBack() {
        app.show("/browseGames");
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
        editIconBlueImage = null;
        editIconBlackImage = null;
        deleteIconRedImage = null;
        deleteIconBlackImage = null;
    }
}
