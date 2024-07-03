package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.GangComponent;
import de.uniks.stp24.component.menu.GangDeletionComponent;
import de.uniks.stp24.component.menu.TraitComponent;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.model.Gang;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.SaveLoadService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
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
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

import static de.uniks.stp24.service.Constants.empireTemplatesEnglish;
import static de.uniks.stp24.service.Constants.empireTemplatesGerman;
import static de.uniks.stp24.service.Constants.colorsArray;

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

    @SubComponent
    @Inject
    public GangDeletionComponent gangDeletionComponent;

    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    @Inject
    public Provider<GangComponent> gangComponentProvider;
    private final ObservableList<Gang> gangs = FXCollections.observableArrayList();

    public Provider<TraitComponent> traitComponentProviderAll = () -> new TraitComponent(this, true, false);
    public Provider<TraitComponent> traitComponentProviderChoosen= () -> new TraitComponent(this, false, true);
    public Provider<TraitComponent> traitComponentProviderConfirmed= () -> new TraitComponent(this, false, false);
    private final ObservableList<Trait> allTraits = FXCollections.observableArrayList();
    private final ObservableList<Trait> choosenTraits = FXCollections.observableArrayList();
    private final ObservableList<Trait> confirmedTraits = FXCollections.observableArrayList();

    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    ImageView spectatorImage;
    @FXML
    VBox spectatorBox;
    @FXML
    Pane captainContainer;
    @FXML
    Pane traitInfoPane;
    @FXML
    Pane buttonsPane;
    @FXML
    ListView<Gang> gangsListView;
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
    Button editButton;
    @FXML
    Button cancelChangesButton;
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
    ListView<Trait> confirmedTraitsListView;
    @FXML
    AnchorPane traitsBox;
    @FXML
    Label traitInfoName;
    @FXML
    Label traitInfoEffects;
    @FXML
    Label traitInfoConflicts;

    boolean lockFlag = false;
    boolean lockPortrait = false;
    boolean lockName = false;
    boolean lockDescription = false;
    boolean lockTraits = false;
    boolean lockColor = false;
    int descriptionIndex = 0;
    int nameIndex = 0;
    int typeIndex = 0;

    Random rand = new Random();

    ArrayList<String> flagsList = new ArrayList<>();
    ArrayList<String> portraitsList = new ArrayList<>();
    ArrayList<String> colorsList = new ArrayList<>();
    String resourcesPaths = "/de/uniks/stp24/assets/";
    String flagsFolderPath = "flags/flag_";
    String portraitsFolderPath = "portraits/captain_";
    String defaultGangName = "Buccaneers";
    int imagesCount = 16;
    int flagImageIndex = 0;
    int portraitImageIndex = 0;
    int colorIndex = 0;

    int traitsCost = 0;
    int traitsLimit = 5;
    private Trait[] traitsPreset;

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

        this.colorsList.addAll(Arrays.asList(colorsArray));
        for (int i = 0; i <= imagesCount; i++) {
            this.flagsList.add(resourcesPaths + flagsFolderPath + i + ".png");
            this.portraitsList.add(resourcesPaths + portraitsFolderPath + i + ".png");
        }

        this.saveLoadService.setUserId(tokenStorage.getUserId());
        this.gangs.addAll(this.saveLoadService.loadGangs());

        this.subscriber.subscribe(presetsApiService.getTraitsPreset(),
                result -> {
                    traitsPreset = result;
                    allTraits.setAll(traitsPreset);
                },
        error -> System.out.println("error with loading presets"));
    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
        bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.intro"));
        captainContainer.setMouseTransparent(true);
        bubbleComponent.setMouseTransparent(true);
    }

    @OnRender
    public void render() {
        buttonsPane.setPickOnBounds(false);

        subscriber.subscribe(lobbyService.getMember(gameID, tokenStorage.getUserId()),
                result -> {
                    Empire playerEmpire = result.empire();
                    if (Objects.nonNull(playerEmpire)) {
                        ArrayList<Trait> playerTraits = new ArrayList<>();
                        for (String traitName : playerEmpire.traits()) {
                            for (Trait trait : allTraits) {
                                if (trait.id().equals(traitName))
                                    playerTraits.add(trait);
                            }
                        }
                        Gang playerGang = new Gang(playerEmpire.name(),
                                playerEmpire.flag(),
                                flagsList.get(playerEmpire.flag()),
                                playerEmpire.portrait(),
                                portraitsList.get(playerEmpire.portrait()),
                                playerEmpire.description(),
                                playerEmpire.color(),
                                colorsList.indexOf(playerEmpire.color()),
                                playerTraits);
                        applyInputs(playerGang);
                        spectatorBox.setVisible(false);
                        selectGangInListView(playerGang);
                    } else {
                        spectatorBox.setVisible(true);
                        spectatorImage.setImage(imageCache.get("icons/spectatorSign.png"));
                    }
                },
                error -> System.out.println("Error while handling player data"));

        changeNodesVisibility(false, creationBox, deletePane, editButton, showDeletePaneButton, traitsBox);

        this.selectedTraitsListView.setItems(this.choosenTraits);
        this.selectedTraitsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.traitComponentProviderChoosen));
        this.allTraitsListView.setItems(this.allTraits);
        this.allTraitsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.traitComponentProviderAll));
        this.confirmedTraitsListView.setItems(this.confirmedTraits);
        this.confirmedTraitsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.traitComponentProviderConfirmed));

        this.gangsListView.setItems(this.gangs);
        this.gangsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.gangComponentProvider));
        gangsListView.setOnMouseClicked(event -> {
            Gang gang = gangsListView.getSelectionModel().getSelectedItem();
            applyInputs(gang);
            traitsBox.setVisible(false);
            changeEditNodes(false, false);
            showCreationButton.setVisible(true);
        });
    }

    private void selectGangInListView(Gang playerGang) {
        Gang sameGang = null;
        for (Gang gang : gangs) {
            if (areGangsEquals(gang, playerGang)) {
                sameGang = gang;
                break;
            }
        }
        if (Objects.isNull(sameGang)) {
            sameGang = playerGang;
            gangs.add(sameGang);
            saveLoadService.saveGang(gangs);
        }
        this.gangsListView.getSelectionModel().select(sameGang);
        changeEditNodes(false, false);
        changeNodesVisibility(true, editButton, showDeletePaneButton);
    }

    private static boolean areGangsEquals(Gang gang1, Gang gang2) {
        ArrayList<String> traits1 = new ArrayList<>();
        ArrayList<String> traits2 = new ArrayList<>();

        if (Objects.nonNull(gang1.traits())) {
            for (Trait trait : gang1.traits()) {
                traits1.add(trait.id());
            }
        }

        if (Objects.nonNull(gang2.traits())) {
            for (Trait trait : gang2.traits()) {
                traits2.add(trait.id());
            }
        }

        return gang1.name().equals(gang2.name())
                && gang1.flagIndex() == gang2.flagIndex()
                && gang1.portraitIndex() == gang2.portraitIndex()
                && gang1.description().equals(gang2.description())
                && gang1.color().equals(gang2.color())
                && traits1.equals(traits2);
    }

    private void applyInputs(Gang gang) {
        showCreationPane(false);
        gangNameText.setText(gang.name());
        flagImageIndex = gang.flagIndex() % flagsList.size();
        flagImage.setImage(imageCache.get(flagsList.get(flagImageIndex)));
        portraitImageIndex = gang.portraitIndex() % portraitsList.size();
        portraitImage.setImage(imageCache.get(portraitsList.get(portraitImageIndex)));
        gangDescriptionText.setText(gang.description());
        colorIndex = gang.colorIndex() % colorsList.size();
        colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
        confirmedTraits.clear();
        if (Objects.nonNull(gang.traits())) confirmedTraits.setAll(gang.traits());
    }

    private void updateTraitLimitText() {
        traitsLimitText.setText(traitsCost + "/" + traitsLimit);
    }

    private void changeEditNodes(boolean show, boolean isNewGang) {
        changeNodesVisibility(show,
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

        creationBox.setVisible(true);
        changeNodesVisibility(false, spectatorBox, traitsBox);
        spectatorBox.setVisible(false);

        createButton.setVisible(isNewGang && show);
        changeNodesVisibility(!isNewGang && show, confirmButton, cancelChangesButton);

        changeNodesVisibility(!show,
                selectButton,
                editButton,
                showDeletePaneButton);

        setTextEditable(show, gangNameText, gangDescriptionText);

        chooseTraitsButton.setDisable(!show);
    }

    public void cancelChanges() {
        Gang gang =gangsListView.getSelectionModel().getSelectedItem();
        applyInputs(gang);
        selectGangInListView(gang);
        showCreationButton.setVisible(true);
    }

    public void back() {
        patchEmpire(null);
    }

    private Gang getInputGang() {
        String gangName = gangNameText.getText();
        if (gangNameText.getText().isEmpty()) gangName = defaultGangName;
        flagImageIndex = flagImageIndex % flagsList.size();
        portraitImageIndex = portraitImageIndex % portraitsList.size();
        colorIndex = colorIndex % colorsList.size();
        ArrayList<Trait> gangsTraits = new ArrayList<>(confirmedTraits);
        return new Gang(gangName, flagImageIndex, flagsList.get(flagImageIndex), portraitImageIndex, portraitsList.get(portraitImageIndex), gangDescriptionText.getText(), colorsList.get(colorIndex), colorIndex, gangsTraits);
    }

    public void edit() {
        changeEditNodes(true, false);
        showCreationButton.setVisible(false);
    }

    public void select() {
        Gang gang = this.gangsListView.getSelectionModel().getSelectedItem();

        ArrayList<String> gangsTraits = new ArrayList<>();
        for (Trait chosen : confirmedTraits) {
            gangsTraits.add(chosen.id());
        }

        Empire empire = new Empire(gang.name(), gang.description(), gang.color(),
                gang.flagIndex() % this.flagsList.size(), gang.portraitIndex() % this.portraitsList.size(),
                gangsTraits, "uninhabitable_0");

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
        gangs.remove(index);

        Gang gang = getInputGang();
        gangs.add(index, gang);

        gangsListView.getSelectionModel().select(index);

        saveLoadService.saveGang(gangs);

        changeEditNodes(false, true);
        showCreationButton.setVisible(true);
    }

    public void delete() {
        int index = gangsListView.getSelectionModel().getSelectedIndex();
        gangs.remove(index);
        saveLoadService.saveGang(gangs);
        showCreationPane(true);
        changeNodesVisibility(false, confirmButton, selectButton);
    }

    public void cancel() {
        deletePane.setVisible(false);
        creationBox.setEffect(null);
    }

    public void showCreationPane(boolean edit) {
        resetCreationPane();
        changeEditNodes(edit, edit);
    }

    public void showCreationPane() {
       showCreationPane(true);
    }

    public void showDeletePane() {
        Gang gang = gangsListView.getSelectionModel().getSelectedItem();
        gangDeletionComponent.setWarningText(gang.name());
        popup.showPopup(deletePane, gangDeletionComponent);
        popup.setBlur(gangsListView, creationBox);
    }

    public void create() {
        Gang gang = getInputGang();
        selectGangInListView(gang);
    }

    public void resetCreationPane() {
        flagImageIndex = 0;
        portraitImageIndex = 0;
        colorIndex = 0;
        flagImage.setImage(imageCache.get(flagsList.get(flagImageIndex)));
        portraitImage.setImage(imageCache.get(portraitsList.get(portraitImageIndex)));
        colorField.setStyle("-fx-background-color: " + colorsList.get(colorIndex));
        gangNameText.setText("");
        gangDescriptionText.setText("");
        confirmedTraits.clear();
    }

    public void showLastFlag() {
        flagImageIndex = flagImageIndex - 1 >= 0 ? flagImageIndex - 1 : flagsList.size() - 1;
        flagImage.setImage(imageCache.get(flagsList.get(flagImageIndex)));
    }

    public void showNextFlag() {
        flagImageIndex = flagImageIndex + 1 < flagsList.size() ? flagImageIndex + 1 : 0;
        flagImage.setImage(imageCache.get(flagsList.get(flagImageIndex)));
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
        portraitImage.setImage(imageCache.get(portraitsList.get(portraitImageIndex)));
    }

    public void showNextPortrait() {
        portraitImageIndex = portraitImageIndex + 1 < portraitsList.size() ? portraitImageIndex + 1 : 0;
        portraitImage.setImage(imageCache.get((portraitsList.get(portraitImageIndex))));
    }

    public void randomize() {
        if (!lockFlag) {
            flagImageIndex = rand.nextInt(0, flagsList.size());
            flagImage.setImage(imageCache.get(flagsList.get(flagImageIndex)));
        }

        if (!lockPortrait) {
            portraitImageIndex = rand.nextInt(0, portraitsList.size());
            portraitImage.setImage(imageCache.get(portraitsList.get(portraitImageIndex)));
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
            name = gangNameText.getText();
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

        if (!lockTraits) {
            resetTraitsLists();
            traitsCost = 0;
            Trait randomTrait;

            /*
            allTraits can still have traits with none of them being choosable, if we choose a Trait that has conflict
            with all other Traits or if the score goes overlimit if choose other trait
             */
            ArrayList<Trait> possibleTraits = new ArrayList<>(allTraits);
            while (!possibleTraits.isEmpty() && traitsCost < traitsLimit && choosenTraits.size() < 5) {
                randomTrait = possibleTraits.get(rand.nextInt(possibleTraits.size()));
                if (canAddTrait(randomTrait)) {
                    addTraitToEmpire(randomTrait);
                }
                possibleTraits.remove(randomTrait);
            }

            if (!traitsBox.isVisible()) {
                traitsConfirm();
            }
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
        lockTraits = !lockTraits;
    }

    public void chooseTraits() {
        resetTraitsLists();
        choosenTraits.setAll(confirmedTraits);
        allTraits.removeAll(choosenTraits);
        choosenTraits.forEach(trait -> traitsCost+=trait.cost());
        updateTraitLimitText();
        traitsBox.setVisible(true);
        setButtonsDisable(true);
    }

    private void setButtonsDisable(boolean disable) {
        changeNodesDisability(disable,
                confirmButton,
                createButton,
                selectButton,
                editButton,
                showDeletePaneButton,
                showCreationButton,
                backButton,
                chooseTraitsButton,
                cancelChangesButton);
    }

    public void traitsConfirm() {
        bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.intro"));
        confirmedTraits.clear();
        confirmedTraits.addAll(choosenTraits);
        traitsReturn();
        setButtonsDisable(false);
    }

    public void traitsReturn() {
        bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.intro"));
        resetTraitsLists();
        traitsBox.setVisible(false);
        setButtonsDisable(false);
    }

    public void addTrait(Trait trait) {
        if (canAddTrait(trait)) {
            addTraitToEmpire(trait);
        }
    }

    private void addTraitToEmpire(Trait trait) {
        bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.intro"));
        allTraits.remove(trait);
        choosenTraits.add(trait);
        traitsCost += trait.cost();
        updateTraitLimitText();
    }

    public void deleteTrait(Trait trait) {
        bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.intro"));
        if (traitsCost - trait.cost() <= traitsLimit) {
            allTraits.add(trait);
            choosenTraits.remove(trait);
            traitsCost -= trait.cost();
            updateTraitLimitText();
        } else {
            bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.scoreOverLimit"));
        }
    }

    public boolean canAddTrait(Trait trait) {
        if (traitsCost + trait.cost() <= traitsLimit && choosenTraits.size() < 5) {
            for (Trait chosen : choosenTraits) {
                if (Objects.nonNull(chosen.conflicts())) {
                    for (String conflict : chosen.conflicts()) {
                        if (conflict.equals(trait.id())) {
                            bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.conflict").replace("{conflict1}", '"' + chosen.id() + '"').replace("{conflict2}", '"' + trait.id() + '"'));
                            return false;
                        }
                    }
                }
            }
           return true;
        }
        if (traitsCost + trait.cost() > traitsLimit) {
            bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.scoreOverLimit"));
        } else if (choosenTraits.size() >= 5) {
            bubbleComponent.setCaptainText(resources.getString("pirate.empireScreen.countOverLimit"));
        }
        return false;
    }

    public void resetTraitsLists() {
        choosenTraits.clear();
        allTraits.clear();
        allTraits.addAll(traitsPreset);
        traitsCost = 0;
    }

    public void showTraitDetails(Trait trait) {
        traitInfoPane.setVisible(true);

        traitInfoName.setText(trait.id());

        StringBuilder effectsText = new StringBuilder("effects\n");
        if (Objects.nonNull(trait.effects())) {
            for (EffectDto effect : trait.effects()) {
                String variable = effect.variable();
                String type = "";
                if (effect.bonus() != 0.00) {
                    if (effect.bonus() > 0) {
                        type = "+";
                    }
                    type += effect.bonus() + " ";
                } else if (effect.multiplier() != 0.00) {
                    type = "*" + effect.multiplier() + " ";
                }
                effectsText.append(type).append(variablesResourceBundle.getString(variable)).append("\n");
            }
        }
        traitInfoEffects.setText(effectsText.toString());

        StringBuilder conflictsText = new StringBuilder("conflicts\n");
        if (Objects.nonNull(trait.conflicts())) {
            for (String conflict : trait.conflicts()) {
                conflictsText.append(conflict).append("\n");
            }
        }
        traitInfoConflicts.setText(conflictsText.toString());
    }

    public void unShowTraitDetails() {
        traitInfoPane.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        portraitImage = null;
    }
}
