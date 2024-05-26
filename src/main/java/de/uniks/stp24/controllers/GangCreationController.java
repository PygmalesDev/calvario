package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.component.GangComponent;
import de.uniks.stp24.model.GangElement;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.SaveLoadService;
import de.uniks.stp24.service.TokenStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static de.uniks.stp24.service.Constants.empireTemplates;

@Title("Gang Creation")
@Controller
public class GangCreationController {
    @Inject
    App app;

    @Inject
    SaveLoadService saveLoadService;

    @Inject
    LobbyService lobbyService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;

    @Inject
    public Provider<GangComponent> gangComponentProvider;

    private final ObservableList<GangElement> gangElements = FXCollections.observableArrayList();

    @Inject
    ImageCache imageCache;

    @Inject
    @Resource
    ResourceBundle resource;

    @FXML
    ListView<GangElement> gangsListView;
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
    Text toBeDeletedGangName;
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
    Button cancelButton;
    @FXML
    Button randomizeButton;
    @FXML
    Button deleteButton;
    @FXML
    Button nextFlagButton;
    @FXML
    ToggleButton lockNameButton;
    @FXML
    ToggleButton lockDescriptionButton;

    @Inject
    public GangCreationController() {

    }
    @Param("gameid")
    String gameID;

    @OnInit
    public void init() {
        String[] colorsArray = {"#DC143C", "#0F52BA", "#50C878", "#9966CC", "#FF7F50",
                "#40E0D0", "#FF00FF", "#FFD700", "#C0C0C0", "#4B0082",
                "#36454F", "#F28500", "#E6E6FA", "#008080", "#800000", "#808000"};

        this.colorsList.addAll(Arrays.asList(colorsArray));
        for (int i = 0; i <= imagesCount; i++) {
            this.flagsList.add(this.imageCache.get(resourcesPaths + flagsFolderPath + i + ".png"));
            this.portraitsList.add(this.imageCache.get(resourcesPaths + portraitsFolderPath + i + ".png"));
        }

        this.saveLoadService.loadGangs().forEach(gang ->
                this.gangElements.add(new GangElement(gang,
                        this.flagsList.get(gang.flagIndex()),
                        this.portraitsList.get(gang.portraitIndex()))));
    }

    @OnRender
    public void render() {
        creationBox.setVisible(false);
        deletePane.setVisible(false);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
        this.gangsListView.setItems(this.gangElements);
        this.gangsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.gangComponentProvider));
        gangsListView.setOnMouseClicked(event -> {
            GangElement gangComp = gangsListView.getSelectionModel().getSelectedItem();
            if (gangComp.gang() != null) {
                Gang gang = gangComp.gang();
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
        Gang gang = this.gangsListView.getSelectionModel().getSelectedItem().gang();

        this.subscriber.subscribe(this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()), result -> {
            Empire empire = null;

            if (Objects.nonNull(gang)) empire = new Empire(gang.name(), gang.description(), gang.color(),
                    gang.flagIndex()%this.flagsList.size(), gang.portraitIndex()%this.portraitsList.size(),
                    "uninhabitable_0", new String[]{});

            this.subscriber.subscribe(this.lobbyService.updateMember(
                    this.gameID, this.tokenStorage.getUserId(),result.ready(), empire), result2 ->
                        app.show("/lobby", Map.of("gameid", this.gameID)));
        });

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
        gangElements.remove(index);

        Gang gang = getInputGang();
        gangElements.add(index, createGangElement(gang));

        ObservableList<Gang> gangs = FXCollections.observableArrayList();
        gangElements.forEach(gangElement -> gangs.add(gangElement.gang()));
        saveLoadService.saveGang(gangs);

        showCreationPane();
    }

    public void delete() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangElements.remove(index);

        saveLoadService.saveGang(createGangsObservableList());

        showCreationPane();
        cancel();
    }

    private GangElement createGangElement(Gang gang) {
        return new GangElement(gang, this.flagsList.get(gang.flagIndex()), this.portraitsList.get(gang.portraitIndex()));
    }

    private ObservableList<Gang> createGangsObservableList() {
        ObservableList<Gang> gangs = FXCollections.observableArrayList();
        gangElements.forEach(gangElement -> gangs.add(gangElement.gang()));
        return gangs;
    }

    public void cancel() {
        deletePane.setVisible(false);
        creationBox.setEffect(null);
    }

    public void showCreationPane() {
        creationBox.setVisible(true);
        resetCreationPane();
        createButton.setVisible(true);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
    }

    public void showDeletePane() {
        creationBox.setEffect(new BoxBlur());
        deletePane.setVisible(true);
        GangElement gang = gangsListView.getSelectionModel().getSelectedItem();
        toBeDeletedGangName.setText(gang.gang().name());
    }

    public void create() {
        Gang gang = getInputGang();

        gangElements.add(createGangElement(gang));
        saveLoadService.saveGang(createGangsObservableList());

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
                secondName = " of " + empireTemplates.get("Suffix")[rand.nextInt(0, empireTemplates.get("Suffix").length)] +
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
        //portraitsList = null;
        //flagsList = null;
    }
}
