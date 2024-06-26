package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.component.menu.GangComponent;
import de.uniks.stp24.component.menu.GangDeletionComponent;
import de.uniks.stp24.component.menu.TraitComponent;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.model.GangElement;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.SaveLoadService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

import static de.uniks.stp24.service.Constants.empireTemplatesEnglish;
import static de.uniks.stp24.service.Constants.empireTemplatesGerman;

@Title("%create.island")
@Controller
public class GangCreationController extends BasicController {
    @Param("gameid")
    String gameID;

    @Inject
    SaveLoadService saveLoadService;
    @Inject
    PresetsApiService presetsApiService;
    @Inject
    LobbyService lobbyService;
    @Inject
    PopupBuilder popupBuilder;
    @Inject
    ImageCache imageCache;

    @Inject
    @Resource
    ResourceBundle resource;

    @SubComponent
    @Inject
    public GangDeletionComponent gangDeletionComponent;

    @Inject
    public Provider<GangComponent> gangComponentProvider;
    private final ObservableList<GangElement> gangElements = FXCollections.observableArrayList();

    @Inject
    public Provider<TraitComponent> traitComponentProvider = () -> new TraitComponent(this);
    private final ObservableList<Trait> allTraits = FXCollections.observableArrayList();
    private final ObservableList<Trait> choosenTraits = FXCollections.observableArrayList();
    private final ObservableList<Trait> choosenAndConfirmedTraits = FXCollections.observableArrayList();

    @FXML
    ListView<GangElement> gangsListView;
    @FXML
    Pane creationBox;
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
    public Button editButton;
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
    @FXML
    ToggleButton lockFlagButton;
    @FXML
    ToggleButton lockPortraitButton;
    @FXML
    ToggleButton lockColorButton;
    @FXML
    Button selectButton;
    @FXML
    Button confirmButton;
    @FXML
    Button chooseTraitsButton;
    @FXML
    ToggleButton lockTraitsButton;
    @FXML
    Button traitsConfirmButton;
    @FXML
    Button traitsReturnButton;
    @FXML
    Text traitsLimitText;
    @FXML
    ListView<Trait> allTraitsListView;
    @FXML
    ListView<Trait> selectedTraitsListView;
    @FXML
    AnchorPane traitsBox;
    ArrayList<Node> editNodes = new ArrayList<>();

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
    PopupBuilder popup = new PopupBuilder();

    @Inject
    public GangCreationController() {

    }

    @OnInit
    public void init() {
        if (prefService.getLocale().equals(Locale.ENGLISH)) {
            empireTemplates = empireTemplatesEnglish;
        } else {
            empireTemplates = empireTemplatesGerman;
        }

        this.gangDeletionComponent.setGangCreationController(this);

        String[] colorsArray = {"#DC143C", "#0F52BA", "#50C878", "#9966CC", "#FF7F50",
                "#40E0D0", "#FF00FF", "#FFD700", "#C0C0C0", "#4B0082",
                "#36454F", "#F28500", "#E6E6FA", "#008080", "#800000", "#808000"};

        this.colorsList.addAll(Arrays.asList(colorsArray));
        for (int i = 0; i <= imagesCount; i++) {
            this.flagsList.add(this.imageCache.get(resourcesPaths + flagsFolderPath + i + ".png"));
            this.portraitsList.add(this.imageCache.get(resourcesPaths + portraitsFolderPath + i + ".png"));
        }

        this.saveLoadService.loadGangs().forEach(gang -> this.gangElements.add(createGangElement(gang)));
        this.subscriber.subscribe(presetsApiService.getTraitsPreset(), allTraits::addAll,
        error -> System.out.println("error with loading presets"));

        System.out.println(allTraits);
    }

    @OnRender
    public void render() {
        // group editor buttons, to make them (in)visible all at once
        editNodes.clear();
        Collections.addAll(editNodes,
                nextColorButton,
                lastColorButton,
                nextPortraitButton,
                lastPortraitButton,
                nextFlagButton,
                lastFlagButton,
                lockColorButton,
                lockPortraitButton,
                lockFlagButton,
                lockDescriptionButton,
                lockNameButton,
                randomizeButton,
                lockTraitsButton);

        creationBox.setVisible(false);
        deletePane.setVisible(false);
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);

        this.selectedTraitsListView.setItems(this.choosenTraits);
        this.selectedTraitsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.traitComponentProvider));
        this.allTraitsListView.setItems(this.allTraits);
        System.out.println(allTraitsListView);
        this.allTraitsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.traitComponentProvider));

        this.gangsListView.setItems(this.gangElements);
        this.gangsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.gangComponentProvider));
        gangsListView.setOnMouseClicked(event -> {
            GangElement gangComp = gangsListView.getSelectionModel().getSelectedItem();
            if (gangComp != null) {
                Gang gang = gangComp.gang();
                creationBox.setVisible(true);
                gangNameText.setText(gang.name());
                flagImageIndex = gang.flagIndex() % flagsList.size();
                flagImage.setImage(flagsList.get(flagImageIndex));
                portraitImageIndex = gang.portraitIndex() % portraitsList.size();
                portraitImage.setImage(portraitsList.get(portraitImageIndex));
                gangDescriptionText.setText(gang.description());
                createButton.setVisible(false);
                editButton.setVisible(true);
                showDeletePaneButton.setVisible(true);
                colorIndex = gang.colorIndex() % colorsList.size();
                colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));

                changeEditNodes(false);
            }
        });
    }

    private void changeEditNodes(Boolean show) {
        editNodes.forEach(node -> node.setVisible(show));

        gangNameText.setEditable(show);
        gangDescriptionText.setEditable(show);
        chooseTraitsButton.setDisable(!show);
    }

    public void back() {
        patchEmpire(null);
    }

    private Gang getInputGang() {
        String gangName = gangNameText.getText();
        if (gangNameText.getText().isEmpty()) gangName = "Buccaneers";
        flagImageIndex = flagImageIndex % flagsList.size();
        portraitImageIndex = portraitImageIndex % portraitsList.size();
        return new Gang(gangName, flagImageIndex, portraitImageIndex, gangDescriptionText.getText(), colorsList.get(colorIndex), colorIndex % colorsList.size());
    }

    public void edit() {
       changeEditNodes(true);
       confirmButton.setVisible(true);
    }

    public void select() {
        GangElement gangElement = this.gangsListView.getSelectionModel().getSelectedItem();
        Empire empire = null;
        if (Objects.nonNull(gangElement))
            empire = new Empire(gangElement.gang().name(), gangElement.gang().description(), gangElement.gang().color(),
                    gangElement.gang().flagIndex() % this.flagsList.size(), gangElement.gang().portraitIndex() % this.portraitsList.size(),
                    new String[]{}, "uninhabitable_0");
        patchEmpire(empire);
    }

    private void patchEmpire(Empire empire) {
        this.subscriber.subscribe(this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()), result ->
                        this.subscriber.subscribe(this.lobbyService.updateMember(
                this.gameID, result.user(), result.ready(), empire), result2 ->
                app.show("/lobby", Map.of("gameid", this.gameID))),
                error -> {
                }
        );
    }

    public void confirm() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangElements.remove(index);

        Gang gang = getInputGang();
        gangElements.add(index, createGangElement(gang));

        ObservableList<Gang> gangs = FXCollections.observableArrayList();
        gangElements.forEach(gangElement -> gangs.add(gangElement.gang()));
        saveLoadService.saveGang(gangs);

        showCreationPane();
        confirmButton.setVisible(false);
    }

    public void delete() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangElements.remove(index);
        saveLoadService.saveGang(createGangsObservableList());
        resetCreationPane();
        showCreationPane();
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
        changeEditNodes(true);
    }

    public void showDeletePane() {
        GangElement gangElement = gangsListView.getSelectionModel().getSelectedItem();
        if (Objects.nonNull(gangElement)) {
            gangDeletionComponent.setWarningText(gangElement.gang().name());
            popup.showPopup(deletePane, gangDeletionComponent);
            popup.setBlur(gangsListView, creationBox);
        }
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
        editButton.setVisible(false);
        showDeletePaneButton.setVisible(false);
        createButton.setVisible(true);
    }

    public void chooseTraits() {
        traitsBox.setVisible(true);
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

    public void lockTraits() {
    }

    public void traitsConfirm() {
    }

    public void traitsReturn() {
        traitsBox.setVisible(false);
    }

    public void addTrait(Trait trait) {
        System.out.println(trait);
        System.out.println(allTraits);
        allTraits.removeIf(element -> element.id().equals(trait.id()));
        System.out.println(allTraits);
        choosenTraits.add(trait);
    }

    public void deleteTrait(Trait trait) {
        allTraits.add(trait);
        choosenTraits.remove(trait);
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        portraitImage = null;
    }
}
