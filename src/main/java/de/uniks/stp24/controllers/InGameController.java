package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import de.uniks.stp24.service.PopupBuilder;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.jetbrains.annotations.NotNull;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {
    @FXML
    StackPane explanationContainer;
    @FXML
    Pane shadow;
    @FXML
    StackPane eventContainer;
    @FXML
    public Button showStorageButton;
    @FXML
    Button showEmpireOverviewButton;
    @FXML
    public HBox storageButtonsBox;

    @FXML
    public Group group;
    @FXML
    public StackPane overviewContainer;
    @FXML
    ScrollPane mapPane;
    @FXML
    public ScrollPane mapScrollPane;
    @FXML
    public Pane mapGrid;
    @FXML
    public StackPane zoomPane;
    @FXML
    StackPane deleteStructureWarningContainer;
    @FXML
    StackPane siteProperties;
    @FXML
    StackPane buildingProperties;
    @FXML
    StackPane buildingsWindow;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    public StackPane storageOverviewContainer;
    @FXML
    StackPane clockComponentContainer;

    @Inject
    public EventService eventService;
    @Inject
    TimerService timerService;
    @Inject
    InGameService inGameService;
    @Inject
    GamesService gamesService;
    @Inject
    LobbyService lobbyService;
    @Inject
    EmpireService empireService;
    @Inject
    public IslandsService islandsService;
    @Inject
    ResourcesService resourceService;
    @Inject
    ExplanationService explanationService;


    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;
    @SubComponent
    @Inject
    public OverviewSitesComponent overviewSitesComponent;
    @SubComponent
    @Inject
    public OverviewUpgradeComponent overviewUpgradeComponent;
    @SubComponent
    @Inject
    public StorageOverviewComponent storageOverviewComponent;
    @SubComponent
    @Inject
    public VariableExplanationComponent variableExplanationComponent;
    @SubComponent
    @Inject
    public ClockComponent clockComponent;
    @SubComponent
    @Inject
    public DeleteStructureComponent deleteStructureComponent;
    @SubComponent
    @Inject
    public EventComponent eventComponent;
    @SubComponent
    @Inject
    public BuildingPropertiesComponent buildingPropertiesComponent;
    @SubComponent
    @Inject
    public BuildingsWindowComponent buildingsWindowComponent;
    @SubComponent
    @Inject
    public SitePropertiesComponent sitePropertiesComponent;

    List<IslandComponent> islandComponentList;
    Map<String, IslandComponent> islandComponentMap;

    @Inject
    public Subscriber subscriber;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    EventListener eventListener;
    @Inject
    public InGameController() {
        lastUpdate = "";
    }

    @Inject
    public GameSystemsApiService gameSystemsApiService;

    public IslandComponent selectedIsland;

    boolean pause = false;

    String gameID;
    String empireID;
    String lastUpdate;
    double scale = 1.0;
    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();
    public ArrayList<String> flagsPath = new ArrayList<>();
    String resourcesPaths = "/de/uniks/stp24/assets/";
    String flagsFolderPath = "flags/flag_";
    PopupBuilder popupBuildingProperties = new PopupBuilder();
    PopupBuilder popupBuildingWindow = new PopupBuilder();
    PopupBuilder popupSiteProperties = new PopupBuilder();
    PopupBuilder popupDeleteStructure = new PopupBuilder();

    @OnInit
    public void init() {
        overviewSitesComponent.setIngameController(this);
        overviewUpgradeComponent.setIngameController(this);
        buildingsWindowComponent.setInGameController(this);
        buildingPropertiesComponent.setInGameController(this);
        sitePropertiesComponent.setInGameController(this);
        deleteStructureComponent.setInGameController(this);

        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();
        //Todo: Outprint for Swagger - can be deleted later
        System.out.println(this.gameID);
        System.out.println(empireID);

        GameStatus gameStatus = inGameService.getGameStatus();
        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_PAUSED"));

        PropertyChangeListener callHandleShowSettings = this::handleShowSettings;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_SETTINGS, callHandleShowSettings);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_SETTINGS"));

        this.subscriber.subscribe(inGameService.loadUpgradePresets(),
                result -> islandAttributes.setSystemPresets(result),
                error -> System.out.println("error in getEmpire in inGame"));

        this.subscriber.subscribe(inGameService.loadBuildingPresets(),
                result -> islandAttributes.setBuildingPresets(result),
                error -> System.out.println("error in getEmpire in inGame"));

        this.subscriber.subscribe(inGameService.loadDistrictPresets(),
                result -> islandAttributes.setDistrictPresets(result),
                error -> System.out.println("error in getEmpire in inGame"));

        if (!tokenStorage.isSpectator()) {
            this.subscriber.subscribe(empireService.getEmpire(gameID, empireID),
                    result -> islandAttributes.setEmpireDto(result),
                    error -> System.out.println("error in getEmpire in inGame"));
            createEmpireListener();
        }

        for (int i = 0; i <= 16; i++) {
            this.flagsPath.add(resourcesPaths + flagsFolderPath + i + ".png");
        }
    }

    private void handleShowSettings(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            Boolean settings = (Boolean) propertyChangeEvent.getNewValue();
            if (settings) {
                showSettings();
            } else {
                unShowSettings();
            }
        }
    }

    private void handlePauseChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            pause = (Boolean) propertyChangeEvent.getNewValue();
            if (pause) {
                pauseGame();
                shadow.setVisible(true);
                shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
            } else {
                resumeGame();
                shadow.setVisible(false);
            }
        }
    }

    @OnRender
    public void render() {
        buildingProperties.setMouseTransparent(true);
        buildingsWindow.setMouseTransparent(true);
        siteProperties.setMouseTransparent(true);
        deleteStructureWarningContainer.setMouseTransparent(true);

        pauseMenuContainer.setMouseTransparent(true);
        pauseMenuContainer.setVisible(false);
        eventComponent.setParent(shadow, eventContainer);
        clockComponent.setToggle(true);
        clockComponentContainer.getChildren().add(clockComponent);
        eventContainer.getChildren().add(eventComponent);
        eventContainer.setVisible(false);
        shadow.setVisible(false);
        eventComponent.setClockComponent(clockComponent);
        pauseMenuContainer.getChildren().add(pauseMenuComponent);

        overviewContainer.setVisible(false);
        overviewSitesComponent.setContainer();
        overviewContainer.getChildren().add(overviewSitesComponent);
        overviewContainer.getChildren().add(overviewUpgradeComponent);
        storageOverviewContainer.setVisible(false);
        storageOverviewContainer.getChildren().add(storageOverviewComponent);

        explanationContainer.setVisible(false);
        explanationContainer.getChildren().add(variableExplanationComponent);
        explanationService.setInGameController(this);
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        pause = !pause;
        inGameService.setShowSettings(false);
        inGameService.setPaused(pause);
        if (pause) {
            pauseMenuContainer.setMouseTransparent(false);
            pauseGame();
        } else {
            pauseMenuContainer.setMouseTransparent(true);
            resumeGame();
        }
    }

    @OnKey(code = KeyCode.I)
    public void showIslandOverviewWindows() {
        buildingProperties.setMouseTransparent(false);
        buildingsWindow.setMouseTransparent(false);
        popupBuildingWindow.showPopup(buildingsWindow, buildingsWindowComponent);
    }

    public void showSettings() {
        pauseMenuContainer.getChildren().remove(pauseMenuComponent);
        pauseMenuContainer.getChildren().add(settingsComponent);
    }

    public void unShowSettings() {
        pauseMenuContainer.getChildren().remove(settingsComponent);
        pauseMenuContainer.getChildren().add(pauseMenuComponent);
    }

    public void pauseGame() {
        pauseMenuContainer.setVisible(pause);
    }

    public void resumeGame() {
        pauseMenuContainer.setVisible(pause);
        shadow.setVisible(false);
    }

    /**
     * created and add buttons for storage and island overview
     * there are problems if they are contained in the fxml
     */

    private void createButtonsStorage() {
        if (!tokenStorage.isSpectator()) {
            if (!(Objects.nonNull(showEmpireOverviewButton) && (Objects.nonNull(showStorageButton)))) {
                showEmpireOverviewButton = new Button();
                showEmpireOverviewButton.setPrefHeight(30);
                showEmpireOverviewButton.setPrefWidth(30);
                showEmpireOverviewButton.setOnAction(event -> showEmpireOverview());
                showEmpireOverviewButton.getStyleClass().add("empireOverviewButton");
                showStorageButton = new Button();
                showStorageButton.setPrefHeight(30);
                showStorageButton.setPrefWidth(30);
                showStorageButton.setId("showStorageButton");
                showStorageButton.getStyleClass().add("storageButton");
                showStorageButton.setOnAction(event -> showStorage());
            }
            this.storageButtonsBox.getChildren().addAll(showStorageButton, showEmpireOverviewButton);
        }
    }

    private void showEmpireOverview() {
        System.out.println("button clicked");
    }

    @OnRender
    public void createMap() {
        this.islandComponentList = islandsService.createIslands(islandsService.getListOfIslands());
        this.islandComponentMap = islandsService.getComponentMap();
        mapGrid.setMinSize(islandsService.getMapWidth(), islandsService.getMapHeight());
        islandsService.createLines(this.islandComponentMap).forEach(line -> this.mapGrid.getChildren().add(line));


        this.islandComponentList.forEach(isle -> {
            isle.setInGameController(this);
            isle.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showInfo);
            isle.setScaleX(1.25);
            isle.setScaleY(1.25);
            this.mapGrid.getChildren().add(isle);
        });
        Platform.runLater(this::createButtonsStorage);

        mapScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> zoomPane.setPrefSize(newValue.getWidth(), newValue.getHeight()));
        mapScrollPane.setVvalue(0.5);
        mapScrollPane.setHvalue(0.5);

        /*
         * zoom function working but not perfect!
         * it's necessary to check deltaX and deltaY because 'shiftdown' switches deltas in event
         */
        mapGrid.setOnScroll(event -> {
            if (event.isShiftDown() && (event.getDeltaY() > 0 || event.getDeltaX() > 0)) {
                scale += 0.1;
                scale = Math.min(scale, 1.45);
                event.consume();
            } else if (event.isShiftDown() && (event.getDeltaY() < 0 || event.getDeltaX() < 0)) {
                scale -= 0.1;
                scale = Math.max(scale, 0.75);
                event.consume();
            }
            group.setScaleX(scale);
            group.setScaleY(scale);
        });

    }

    public void showInfo(MouseEvent event) {
        if (event.getSource() instanceof IslandComponent selected) {
            if (tokenStorage.getIsland() == null) {
                tokenStorage.setIsland(selected.getIsland());
            }
            islandAttributes.setIsland(selected.getIsland());
            selectedIsland = selected;
            if (selected.getIsland().owner() != null) {
                showOverview();
                selected.showUnshowRudder();
            }
        }
    }

    public void showOverview() {
        overviewSitesComponent.inputIslandName.setDisable(!Objects.equals(islandAttributes.getIsland().owner(), tokenStorage.getEmpireId()));
        overviewSitesComponent.buildingsComponent.resetPage();
        overviewSitesComponent.buildingsComponent.setGridPane();
        overviewContainer.setVisible(true);
        overviewSitesComponent.sitesContainer.setVisible(true);
        overviewSitesComponent.buildingsButton.setDisable(true);
        inGameService.showOnly(overviewContainer, overviewSitesComponent);
        inGameService.showOnly(overviewSitesComponent.sitesContainer, overviewSitesComponent.buildingsComponent);
        overviewSitesComponent.setOverviewSites();
    }

    @OnKey(code = KeyCode.S)
    public void showStorage() {
        storageOverviewContainer.setVisible(!storageOverviewContainer.isVisible());
    }

    @OnKey(code = KeyCode.SPACE)
    public void resetZoom() {
        scale = 1.0;
        group.setScaleX(scale);
        group.setScaleY(scale);
    }

    public void resetZoomMouse(@NotNull MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            resetZoom();
        }
    }

    @OnDestroy
    public void destroy() {
        islandComponentList.forEach(IslandComponent::destroy);
        islandComponentList = null;
        islandComponentMap = null;
        islandsService.removeDataForMap();
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
                .removePropertyChangeListener(triple.propertyName(), triple.listener()));
        this.subscriber.dispose();
    }

    public void showBuildingInformation(String buildingToAdd) {
        siteProperties.setVisible(false);
        siteProperties.setMouseTransparent(true);
        buildingPropertiesComponent.setBuildingType(buildingToAdd);
        popupBuildingProperties.showPopup(buildingProperties, buildingPropertiesComponent);
    }

    public void handleDeleteStructure(String buildingType) {
        deleteStructureWarningContainer.toFront();
        deleteStructureWarningContainer.setMouseTransparent(false);
        popupDeleteStructure.showPopup(deleteStructureWarningContainer, deleteStructureComponent);
        popupDeleteStructure.setBlur(buildingProperties, buildingsWindow);
        popupBuildingProperties.setBlur(mapScrollPane, siteProperties);
        deleteStructureComponent.handleDeleteStructure(buildingType);
    }

    public void updateAmountSitesGrid() {
        sitePropertiesComponent.displayAmountOfSite();
    }

    public void handleAfterStructureDelete() {
        deleteStructureWarningContainer.setMouseTransparent(true);

        buildingsWindow.setMouseTransparent(false);
        popupDeleteStructure.removeBlur();
        popupBuildingProperties.removeBlur();
        if (!siteProperties.isVisible()) {
            siteProperties.setMouseTransparent(true);
        }
        buildingsWindow.toFront();
    }

    public void createEmpireListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    if (!lastUpdate.equals(event.data().updatedAt())) {
                        islandAttributes.setEmpireDto(event.data());
                        System.out.println("Event -> minerals: " + islandAttributes.getAvailableResources().get("minerals") + " alloys: " + islandAttributes.getAvailableResources().get("alloys"));
                        overviewUpgradeComponent.setUpgradeButton();
                        this.lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorListener")
        );
    }

    public void showBuildingWindow() {
        siteProperties.setVisible(false);
        buildingsWindow.setMouseTransparent(false);
        popupBuildingWindow.showPopup(buildingsWindow, buildingsWindowComponent);
        buildingProperties.setMouseTransparent(false);

    }

    public void showSiteOverview() {
        siteProperties.setMouseTransparent(false);
        buildingsWindow.setVisible(false);
        buildingProperties.setVisible(false);
        popupSiteProperties.showPopup(siteProperties, sitePropertiesComponent);

    }

    public void setSiteType(String siteType) {
        sitePropertiesComponent.setSiteType(siteType);
    }

    public void updateSiteCapacities() {
        overviewSitesComponent.showSites();
    }

    public void setSitePropertiesInvisible() {
        sitePropertiesComponent.setVisible(false);
        buildingProperties.setMouseTransparent(false);
        overviewSitesComponent.buildingsComponent.resetPage();
        overviewSitesComponent.buildingsComponent.setGridPane();
    }

    public void showExplanation(int x, int y){
        explanationContainer.setLayoutX(x);
        explanationContainer.setLayoutY(y);
        explanationContainer.setVisible(true);
    }

    public void unShowExplanation(){
        explanationContainer.setLayoutX(0);
        explanationContainer.setLayoutY(0);
        explanationContainer.setVisible(false);
    }
}
