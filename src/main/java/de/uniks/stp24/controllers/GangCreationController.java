package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.GangComponent;
import de.uniks.stp24.component.menu.GangDeletionComponent;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.service.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import static de.uniks.stp24.service.Constants.empireTemplatesEnglish;
import static de.uniks.stp24.service.Constants.empireTemplatesGerman;

@Title("%create.island")
@Controller
public class GangCreationController extends BasicController {
    @Inject
    SaveLoadService saveLoadService;
    @Inject
    LobbyService lobbyService;
    @Inject
    PopupBuilder popupBuilder;
    @SubComponent
    @Inject
    GangDeletionComponent gangDeletionComponent;
    @Inject
    public Provider<GangComponent> gangComponentProvider;
    @FXML
    ListView<Gang> gangsListView;
    @FXML
    VBox creationBox;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView portraitImage;
    @FXML
    TextField gangNameText;
    @FXML
    TextArea gangDescriptionText;
    @FXML
    Button createButton;
    @FXML
    Button editButton;
    @FXML
    Button showDeletePaneButton;
    @FXML
    Pane deletePane;
    @FXML
    Button nextColorButton;
    @FXML
    Button lastColorButton;
    @FXML
    Pane colorField;

    boolean lockFlag = false;
    boolean lockPortrait = false;
    boolean lockName = false;
    boolean lockDescription = false;
    boolean lockColor = false;
    int descriptionIndex = 0;
    int nameIndex = 0;
    int typeIndex = 0;

    Random rand = new Random();
    
    ArrayList<Image> flagsList = new ArrayList<>();
    ArrayList<Image> portraitsList = new ArrayList<>();
    ArrayList<String> colorsList = new ArrayList<>();
    String resourcesPaths = "/de/uniks/stp24/assets/";
    String flagsFolderPath = "flags/flag_";
    String portraitsFolderPath = "portraits/captain_";
    int imagesCount = 16;
    int flagImageIndex = 0;
    int portraitImageIndex = 0;
    int colorIndex = 0;
    Map<String, String[]> empireTemplates;

    private ObservableList<Gang> gangs;
    PopupBuilder popup = new PopupBuilder();

    // unused FX IDs (declared here to remove warnings from fxml file)
    @FXML
    Button backButton;
    @FXML
    Button showCreationButton;
    @FXML
    Button lastFlagButton;
    @FXML
    Button lastPortraitButton;
    @FXML
    Button nextPortraitButton;
    @FXML
    Button randomizeButton;
    @FXML
    Button nextFlagButton;
    @FXML
    ToggleButton lockNameButton;
    @FXML
    ToggleButton lockDescriptionButton;
    @Param("gameid")
    String gameID;
    @FXML
    ToggleButton lockFlagButton;
    @FXML
    ToggleButton lockPortraitButton;
    @FXML
    ToggleButton lockColorButton;


    @Inject
    public GangCreationController() {

    }

    @OnInit
    public void init(){
        initImages();
        initColors();
        gangDeletionComponent.setGangCreationController(this);
        if(prefService.getLocale().equals(Locale.GERMAN)) {
            empireTemplates = empireTemplatesGerman;
        } else {
            empireTemplates = empireTemplatesEnglish;
        }
        gangs = saveLoadService.loadGangs();
    }

    private void initImages() {
        for (int i = 0; i <= imagesCount; i++) {
            InputStream flagStream = GangCreationController.class.getResourceAsStream(resourcesPaths + flagsFolderPath + i + ".png");
            InputStream portraitStream = GangCreationController.class.getResourceAsStream(resourcesPaths + portraitsFolderPath + i + ".png");
            addImagesToList(flagStream, flagsList);
            addImagesToList(portraitStream, portraitsList);
        }
    }

    public static void addImagesToList(InputStream stream, ArrayList<Image> list) {
        byte[] imageData;
        if (stream != null) {
            try {
                imageData = stream.readAllBytes();
                list.add(new Image(new ByteArrayInputStream(imageData)));
            } catch (IOException e) {
                System.out.println("Could not load image :(");
            }
        } else {
            System.out.println("Resource not found");
        }
    }

    private void initColors() {
        String[] colorsArray = {"#DC143C", "#0F52BA", "#50C878", "#9966CC", "#FF7F50",
                "#40E0D0", "#FF00FF", "#FFD700", "#C0C0C0", "#4B0082",
                "#36454F", "#F28500", "#E6E6FA", "#008080", "#800000", "#808000"};
        colorsList.addAll(Arrays.asList(colorsArray));
    }

    @OnRender
    public void render() {
        creationBox.setVisible(false);
        deletePane.setVisible(false);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
        this.gangsListView.setItems(this.gangs);
        this.gangsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.gangComponentProvider));
        gangsListView.setOnMouseClicked(event -> {
            Gang gang = gangsListView.getSelectionModel().getSelectedItem();
            if (gang != null) {
                creationBox.setVisible(true);
                gangNameText.setText(gang.name());
                flagImageIndex = gang.flagIndex()%flagsList.size();
                flagImage.setImage(flagsList.get(flagImageIndex));
                portraitImageIndex = gang.portraitIndex()%portraitsList.size();
                portraitImage.setImage(portraitsList.get(portraitImageIndex));
                gangDescriptionText.setText(gang.description());
                createButton.setVisible(false);
                editButton.setVisible(true);
                showDeletePaneButton.setVisible(true);
                colorIndex = gang.colorIndex()%colorsList.size();
                colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
            }
        });
    }

    public void back() {
        Gang gang = this.gangsListView.getSelectionModel().getSelectedItem();
        this.subscriber.subscribe(this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()),
          result -> {
            Empire empire = null;

            if (Objects.nonNull(gang)) empire = new Empire(gang.name(), gang.description(), gang.color(),
                    gang.flagIndex()%this.flagsList.size(), gang.portraitIndex()%this.portraitsList.size(),
                    new String[]{},"uninhabitable_0");

            this.subscriber.subscribe(this.lobbyService.updateMember(
                    this.gameID, this.tokenStorage.getUserId(),result.ready(), empire), result2 ->
                        app.show("/lobby", Map.of("gameid", this.gameID)));
        },
          error -> {}
        );
    }

    public Gang getInputGang() {
        String gangName = gangNameText.getText();
        if (gangNameText.getText().isEmpty()) gangName = "Buccaneers";
        flagImageIndex = flagImageIndex%flagsList.size();
        portraitImageIndex = portraitImageIndex%portraitsList.size();
        return new Gang(gangName, flagImageIndex, portraitImageIndex, gangDescriptionText.getText(), colorsList.get(colorIndex), colorIndex%colorsList.size());
    }

    public void edit() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangs.remove(index);
        Gang gang = getInputGang();
        gangs.add(index, gang);
        saveLoadService.saveGang(gangs);
        showCreationPane();
    }

    public void delete() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangs.remove(index);
        saveLoadService.saveGang(gangs);
        resetCreationPane();
    }

    public void showCreationPane() {
        creationBox.setVisible(true);
        resetCreationPane();
        createButton.setVisible(true);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
    }

    public void showDeletePane() {
        Gang gang = gangsListView.getSelectionModel().getSelectedItem();
        gangDeletionComponent.setWarningText(gang.name());
        popup.showPopup(deletePane, gangDeletionComponent);
        popup.setBlur(gangsListView, creationBox);

    }

    public void create() {
        Gang gang = getInputGang();
        gangs.add(gang);
        saveLoadService.saveGang(gangs);
        resetCreationPane();
    }

    public void resetCreationPane() {
        flagImageIndex = 0;
        portraitImageIndex = 0;
        colorIndex = 0;
        flagImage.setImage(flagsList.get(flagImageIndex));
        portraitImage.setImage(portraitsList.get(portraitImageIndex));
        colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
        gangNameText.setText("");
        gangDescriptionText.setText("");
    }

    public void showLastFlag() {
        flagImageIndex = flagImageIndex - 1 >= 0 ? flagImageIndex - 1 : flagsList.size() - 1;
        flagImage.setImage(flagsList.get(flagImageIndex));
    }

    public void showNextFlag() {
        flagImageIndex = flagImageIndex + 1 < flagsList.size() ? flagImageIndex + 1 : 0;
        flagImage.setImage(flagsList.get(flagImageIndex));
    }

    public void showLastColor() {
        colorIndex = colorIndex - 1 >= 0 ? colorIndex - 1 : colorsList.size() - 1;
        colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
    }

    public void showNextColor() {
        colorIndex = colorIndex + 1 < colorsList.size() ? colorIndex + 1 : 0;
        colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
    }

    public void showLastPortrait() {
        portraitImageIndex = portraitImageIndex - 1 >= 0 ? portraitImageIndex - 1 : portraitsList.size() - 1;
        portraitImage.setImage(portraitsList.get(portraitImageIndex));
    }

    public void showNextPortrait() {
        portraitImageIndex = portraitImageIndex + 1 < portraitsList.size() ? portraitImageIndex + 1 : 0;
        portraitImage.setImage(portraitsList.get(portraitImageIndex));
    }

    public void randomize() {
        if (!lockFlag) {
            flagImageIndex = rand.nextInt(0, flagsList.size());
            flagImage.setImage(flagsList.get(flagImageIndex));
        }

        if (!lockPortrait) {
            portraitImageIndex = rand.nextInt(0, portraitsList.size());
            portraitImage.setImage(portraitsList.get(portraitImageIndex));
        }

        String name;
        if (!lockName) {
            nameIndex = rand.nextInt(0, empireTemplates.get("Prefix").length);
            typeIndex = rand.nextInt(0, empireTemplates.get("Type").length);
            name = empireTemplates.get("Prefix")[nameIndex]
                    + " " + empireTemplates.get("Type")[typeIndex];
            String secondName = "";
            if (rand.nextInt(0, 4) == 3)
                secondName = " " + resources.getString("of") + " " + empireTemplates.get("Suffix")[rand.nextInt(0, empireTemplates.get("Suffix").length)] +
                        " " + empireTemplates.get("Definition")[rand.nextInt(0, empireTemplates.get("Definition").length)];
            gangNameText.setText(name + secondName);
        } else {
            name = empireTemplates.get("Prefix")[nameIndex]
                    + " " + empireTemplates.get("Type")[typeIndex];
        }

        if (!lockDescription) {
            descriptionIndex = rand.nextInt(0, empireTemplates.get("Description").length);
            String description = empireTemplates.get("Description")[descriptionIndex]
                    .replace("{NAME}", name);
            gangDescriptionText.setText(description);
        } else {
            String description = empireTemplates.get("Description")[descriptionIndex]
                    .replace("{NAME}", name);
            gangDescriptionText.setText(description);
        }

        if (!lockColor) {
            colorIndex = rand.nextInt(0, colorsList.size());
            colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
        }
    }

    public void lockFlag() {
        lockFlag = !lockFlag;
    }

    public void lockPortrait() {
        lockPortrait = !lockPortrait;
    }

    public void lockName() {
        lockName = !lockName;
    }

    public void lockDescription() {
        lockDescription = !lockDescription;
    }

    public void lockColor() {
        lockColor = !lockColor;
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        portraitImage = null;
    }
}
