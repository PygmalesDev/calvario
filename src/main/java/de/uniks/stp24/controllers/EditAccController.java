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
import javafx.scene.control.Label;
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
    ToggleButton editAvatarButton;

    @FXML
    Button safeAvatarButton;
    @FXML
    Button cancelAvatarButton;

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

    @FXML
    Button randomizeAvatar;

    @FXML
    ToggleButton lockBackgroundButton;
    @FXML
    ToggleButton lockPortraitButton;
    @FXML
    ToggleButton lockFrameButton;

    @FXML
    Label imageCodeLabel;

    @Param("gameid")
    String gameID;

    Random rand = new Random();

    Map<String, String[]> empireTemplates;
    ArrayList<Image> backgroundsList = new ArrayList<>();
    ArrayList<Image> framesList = new ArrayList<>();
    ArrayList<Image> portraitsList = new ArrayList<>();

    Map<String, Integer> avatarMap;


    //TODO add correct paths and correct variable names
    String resourcesPaths = "/de/uniks/stp24/assets/avatar/";
    String backgroundFolderPath = "backgrounds/background_";
    String frameFolderPath = "frames/frame_";
    String portraitsFolderPath = "portraits/portrait_";

    int imagesCount = 9;
    int backgroundImageIndex = 0;
    int frameImageIndex = 0;
    int portraitImageIndex = 0;


    int beforeBackgroundImageIndex = 0;
    int beforeFrameImageIndex = 0;
    int beforePortraitImageIndex = 0;



    boolean lockBackground = false;
    boolean lockPortrait = false;
    boolean lockFrame = false;

    Map<String, Integer> currentAvatarMap = new HashMap<>();

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
    public void render() {
        avatarButtonsVisible(false);
    }


    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(tokenStorage.getName()))
            this.usernameInput.setText(tokenStorage.getName());
        this.errorLabelEditAcc.setText("");

        // this.avatarImage.setImage(imageCache.get(Objects.nonNull(tokenStorage.getAvatar()) ? tokenStorage.getAvatar() : "test/911.png"));

        /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
        avatarMap = tokenStorage.getAvatarMap();
        setImageCode(avatarMap.get("backgroundIndex"), avatarMap.get("portraitIndex"), avatarMap.get("frameIndex"));
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
        backgroundsList = null;
        portraitsList = null;
        framesList = null;
    }

    /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
    private void setImageCode(int backgroundIndex, int potraitIndex, int frameIndex) {
        backgroundImageIndex = backgroundIndex;
        portraitImageIndex = potraitIndex;
        frameImageIndex = frameIndex;

        backgroundImage.setImage(backgroundsList.get(backgroundImageIndex));
        portraitImage.setImage(portraitsList.get(portraitImageIndex));
        frameImage.setImage(framesList.get(frameImageIndex));

        System.out.print(backgroundIndex + ": ");
        System.out.print(potraitIndex + ": ");
        System.out.print(frameIndex + ": ");
        System.out.println();
        imageCodeLabel.setText(getImageCode());

        avatarMap.put("backgroundIndex", backgroundIndex);
        avatarMap.put("portraitIndex", potraitIndex);
        avatarMap.put("frameIndex", frameIndex);
    }

    public String getImageCode() {
        String bachkroundIndexString = String.valueOf(backgroundImageIndex);
        String potraitIndex = String.valueOf(portraitImageIndex);
        String frameIndexString = String.valueOf(frameImageIndex);

        return bachkroundIndexString + potraitIndex + frameIndexString;
    }

    public void showLastBackground() {
        if (Objects.nonNull(lastBackgroundButton)) {
            backgroundImageIndex = backgroundImageIndex - 1 >= 0 ? backgroundImageIndex - 1 : backgroundsList.size() - 1;
            setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        }
    }

    public void showLastPortrait() {
        if (Objects.nonNull(lastPotraitButton)) {
            portraitImageIndex = portraitImageIndex - 1 >= 0 ? portraitImageIndex - 1 : portraitsList.size() - 1;
            setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        }
    }

    public void showLastFrame() {
        if (Objects.nonNull(lastFrameButton)) {
            frameImageIndex = frameImageIndex - 1 >= 0 ? frameImageIndex - 1 : framesList.size() - 1;
            setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        }
    }

    public void showNextBackground() {
        if (Objects.nonNull(nextBackgroundButton)) {
            backgroundImageIndex = backgroundImageIndex + 1 < backgroundsList.size() ? backgroundImageIndex + 1 : 0;
            setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        }
    }

    public void showNextPortrait() {
        if (Objects.nonNull(nextPotraitButton)) {
            portraitImageIndex = portraitImageIndex + 1 < portraitsList.size() ? portraitImageIndex + 1 : 0;
            setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        }
    }

    public void showNextFrame() {
        if (Objects.nonNull(nextFrameButton)) {
            frameImageIndex = frameImageIndex + 1 < framesList.size() ? frameImageIndex + 1 : 0;
            setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        }
    }

    public void changeUserAvatar() {

        if (editAvatarButton.isSelected()) {
            beforeBackgroundImageIndex = backgroundImageIndex;
            beforePortraitImageIndex = portraitImageIndex;
            beforeFrameImageIndex = frameImageIndex;
            avatarButtonsVisible(true);
            editAvatarButton.setStyle("-fx-text-fill: #2B78E4");
        } else {
            cancelAvatarChanges();
            avatarButtonsVisible(false);
            editAvatarButton.setStyle("-fx-text-fill: Black");
        }
    }

    public void safeAvatarChanges() {
        System.out.println(getImageCode());

        subscriber.subscribe(editAccService.changeAvatar(avatarMap),
                result -> {
                    beforeBackgroundImageIndex = backgroundImageIndex;
                    beforePortraitImageIndex = portraitImageIndex;
                    beforeFrameImageIndex = frameImageIndex;
                    resetEditing(usernameInput.getText());
                    avatarButtonsVisible(false);

                }
                , error -> {
                    this.bubbleComponent.setErrorMode(true);
                    this.bubbleComponent.setCaptainText(getErrorInfoText(error));
                });


        avatarButtonsVisible(false);
        editAvatarButton.setStyle("-fx-text-fill: Black");
    }

    public void cancelAvatarChanges() {
        System.out.println(beforeBackgroundImageIndex);
        backgroundImageIndex = beforeBackgroundImageIndex;
        frameImageIndex = beforeFrameImageIndex;
        portraitImageIndex = beforePortraitImageIndex;
        setImageCode(backgroundImageIndex, portraitImageIndex, frameImageIndex);
        avatarButtonsVisible(false);
        editAvatarButton.setStyle("-fx-text-fill: Black");

        System.out.println(tokenStorage.getUserId());
    }

    private void resetAvatarEditing() {
    }

    public void avatarButtonsVisible(boolean visible) {
        safeAvatarButton.setVisible(visible);
        cancelAvatarButton.setVisible(visible);

        lastBackgroundButton.setVisible(visible);
        nextBackgroundButton.setVisible(visible);
        lastFrameButton.setVisible(visible);
        nextFrameButton.setVisible(visible);
        lastPotraitButton.setVisible(visible);
        nextPotraitButton.setVisible(visible);
        imageCodeLabel.setVisible(visible);

        randomizeAvatar.setVisible(visible);

        lockBackgroundButton.setVisible(visible);
        lockPortraitButton.setVisible(visible);
        lockFrameButton.setVisible(visible);
    }


    public ArrayList<Image> getBackgroundsList() {
        return backgroundsList;
    }

    public ArrayList<Image> getFramesList() {
        return framesList;
    }

    public ArrayList<Image> getPortraitsList() {
        return portraitsList;
    }

    public void randomize() {
        if (!lockBackground) {
            this.backgroundImageIndex = rand.nextInt(0, backgroundsList.size());
            backgroundImage.setImage(backgroundsList.get(backgroundImageIndex));
        }

        if (!lockPortrait) {
            portraitImageIndex = rand.nextInt(0, portraitsList.size());
            portraitImage.setImage(portraitsList.get(portraitImageIndex));
        }

        if (!lockFrame) {
            frameImageIndex = rand.nextInt(0, framesList.size());
            frameImage.setImage(framesList.get(frameImageIndex));
        }

        imageCodeLabel.setText(getImageCode());
    }

    public void lockBackground() {
        lockBackground = !lockBackground;
    }

    public void lockPortrait() {
        lockPortrait = !lockPortrait;
    }

    public void lockFrame() {
        lockFrame = !lockFrame;
    }
    /*---------------------------------------- AVATAR EDITING---------------------------------------------------------*/
}
