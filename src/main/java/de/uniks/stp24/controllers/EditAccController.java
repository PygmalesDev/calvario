package de.uniks.stp24.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.GangComponent;
import de.uniks.stp24.component.menu.WarningScreenComponent;
import de.uniks.stp24.model.GangElement;
import de.uniks.stp24.service.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

import static de.uniks.stp24.service.Constants.empireTemplatesEnglish;
import static de.uniks.stp24.service.Constants.empireTemplatesGerman;

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

    /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
    @Inject
    SaveLoadService saveLoadService;
    @Inject
    LobbyService lobbyService;

    @Inject
    public Provider<GangComponent> avatarComponentProvider;

    private final ObservableList<GangElement> avatarElements = FXCollections.observableArrayList();

    @Inject
    ImageCache imageCache;

    @FXML
    ImageView backgroundImage;
    @FXML
    ImageView portraitImage;
    @FXML
    ImageView frameImage;

    @FXML
    Button lastBackgroundButton;
    @FXML
    Button nextBackgroundButton;
    @FXML
    Button lastFrameButton;
    @FXML
    Button nextFrameButton;
    @FXML
    Button lastPotraitButton;
    @FXML
    Button nextPotraitButton;

    @Param("gameid")
    String gameID;

    Random rand = new Random();

    Map<String, String[]> empireTemplates;
    ArrayList<Image> backgroundsList = new ArrayList<>();
    ArrayList<Image> framesList = new ArrayList<>();
    ArrayList<Image> portraitsList = new ArrayList<>();

    //TODO add correct paths and correct variable names
    String resourcesPaths = "/de/uniks/stp24/assets/avatar/";
    String backgroundFolderPath = "backgrounds/background_";
    String frameFolderPath = "frames/frame_";
    String portraitsFolderPath = "portraits/portrait_";

    int imagesCount = 2;
    int backgroundImageIndex = 0;
    int frameImageIndex = 0;
    int portraitImageIndex = 0;

    PopupBuilder popup = new PopupBuilder();
    /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/

    @Inject
    public WarningScreenComponent warningScreen;
    private BooleanBinding editAccIsNotSelected;
    private BooleanBinding warningIsInvisible;
    public Image editIconBlueImage;
    public Image editIconBlackImage;
    public Image deleteIconRedImage;
    public Image deleteIconBlackImage;

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
    public void init() {
        editIconBlueImage = imageCache.get("icons/editBlue.png");
        editIconBlackImage = imageCache.get("icons/editBlack.png");
        deleteIconRedImage = imageCache.get("icons/deleteRed.png");
        deleteIconBlackImage = imageCache.get("icons/deleteBlack.png");
        this.controlResponses = responseConstants.respEditAcc;

        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
        if (prefService.getLocale().equals(Locale.ENGLISH)) {
            empireTemplates = empireTemplatesEnglish;
        } else {
            empireTemplates = empireTemplatesGerman;
        }

        for (int i = 0; i <= imagesCount; i++) {
            this.backgroundsList.add(this.imageCache.get(resourcesPaths + backgroundFolderPath + i + ".png"));
            this.framesList.add(this.imageCache.get(resourcesPaths + frameFolderPath + i + ".png"));
            this.portraitsList.add(this.imageCache.get(resourcesPaths + portraitsFolderPath + i + ".png"));
        }
        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
    }

    @OnRender
    public void render(){
        lastBackgroundButton.setVisible(false);
        nextBackgroundButton.setVisible(false);
        lastFrameButton.setVisible(false);
        nextFrameButton.setVisible(false);
        lastPotraitButton.setVisible(false);
        nextPotraitButton.setVisible(false);
    }


    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(tokenStorage.getName()))
            this.usernameInput.setText(tokenStorage.getName());
        this.errorLabelEditAcc.setText("");

        // this.avatarImage.setImage(imageCache.get(Objects.nonNull(tokenStorage.getAvatar()) ? tokenStorage.getAvatar() : "test/911.png"));

        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
        setImageCode(1,1,1);
        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
    }



    @OnRender
    public void createBindings() {
        this.editAccIsNotSelected = this.changeUserInfoButton.selectedProperty().not();
        this.warningIsInvisible = this.warningScreenContainer.visibleProperty().not();
    }

    @OnRender
    public void changeUserInfo() {
        // If the changeUserButton is selected, username and password can be edited
        if (changeUserInfoButton.isSelected()) {
            passwordInput.setEditable(true);
            usernameInput.setEditable(true);
            cancelChangesButton.setVisible(true);
            saveChangesButton.setVisible(true);
            changeUserInfoButton.setStyle("-fx-text-fill: #2B78E4");
            editIconImageView.setImage(editIconBlueImage);

            /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
            lastBackgroundButton.setVisible(true);
            nextBackgroundButton.setVisible(true);
            lastFrameButton.setVisible(true);
            nextFrameButton.setVisible(true);
            lastPotraitButton.setVisible(true);
            nextPotraitButton.setVisible(true);
            /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/

        } else {
            resetEditing(tokenStorage.getName());
        }
    }

    @OnRender
    public void disableButtons() {
        this.deleteUserButton.disableProperty()
                .bind(Bindings.createBooleanBinding(() -> !editAccIsNotSelected.get(),
                        this.editAccIsNotSelected));
        this.goBackButton.disableProperty()
                .bind(Bindings.createBooleanBinding(() -> !editAccIsNotSelected.get(),
                        this.editAccIsNotSelected));
    }

    @OnRender
    public void changeDeleteButtonView() {
        // delete Button has red text and icon when selected
        this.deleteUserButton.styleProperty().bind(Bindings.createStringBinding(() -> {
            if (warningIsInvisible.get())
                return "-fx-text-fill: Black";
            return "-fx-text-fill: #CF2A27";
        }, this.warningIsInvisible));

        this.deleteIconImageView.imageProperty().bind(Bindings.createObjectBinding(() -> {
            if (warningIsInvisible.get())
                return deleteIconBlackImage;
            return deleteIconRedImage;
        }, this.warningIsInvisible));
    }

    public void saveChanges() {
        // save changed name and/or password of the user and reset the edit account screen afterward
        if (checkIt(usernameInput.getText(), passwordInput.getText())) {
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
            this.bubbleComponent.setCaptainText(getErrorInfoText(passwordInput.getLength() > 8 ? -1 : -2));
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

        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
        lastBackgroundButton.setVisible(false);
        nextBackgroundButton.setVisible(false);
        lastFrameButton.setVisible(false);
        nextFrameButton.setVisible(false);
        lastPotraitButton.setVisible(false);
        nextPotraitButton.setVisible(false);
        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/

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

    /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
    private void setImageCode(int backgroundIndex, int potraitImageIndex, int frameImageIndex) {
        backgroundImage.setImage(backgroundsList.get(backgroundIndex));
        portraitImage.setImage(portraitsList.get(potraitImageIndex));
        frameImage.setImage(framesList.get(frameImageIndex));
    }



    public void showLastBackground() {
        backgroundImageIndex = backgroundImageIndex - 1 >= 0 ? backgroundImageIndex - 1 : backgroundsList.size() - 1;
        backgroundImage.setImage(backgroundsList.get(backgroundImageIndex));
    }

    public void showLastPortrait() {
        if(Objects.nonNull(lastBackgroundButton)){
            portraitImageIndex = portraitImageIndex - 1 >= 0 ? portraitImageIndex - 1 : portraitsList.size() - 1;
            portraitImage.setImage(portraitsList.get(portraitImageIndex));
        }
    }

    public void showLastFrame() {
        if(Objects.nonNull(lastFrameButton)){
            frameImageIndex = frameImageIndex - 1 >= 0 ? frameImageIndex - 1 : framesList.size() - 1;
            frameImage.setImage(framesList.get(frameImageIndex));
        }
    }

    public void showNextBackground() {
        if(Objects.nonNull(nextBackgroundButton)){
            backgroundImageIndex = backgroundImageIndex + 1 < backgroundsList.size() ? backgroundImageIndex + 1 : 0;
            backgroundImage.setImage(backgroundsList.get(backgroundImageIndex));
        }
    }

    public void showNextPortrait() {
        if(Objects.nonNull(nextPotraitButton)){
            portraitImageIndex = portraitImageIndex + 1 < portraitsList.size() ? portraitImageIndex + 1 : 0;
            portraitImage.setImage(portraitsList.get(portraitImageIndex));
        }
    }

    public void showNextFrame() {
        if(Objects.nonNull(nextFrameButton)){
            frameImageIndex = frameImageIndex + 1 < framesList.size() ? frameImageIndex + 1 : 0;
            frameImage.setImage(framesList.get(frameImageIndex));

        }
    }


    /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
}
