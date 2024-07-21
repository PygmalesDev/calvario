package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.technology.TechnologyOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.rest.GameSystemsApiService;
import static de.uniks.stp24.service.Constants.*;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {
    @FXML
    public AnchorPane rootPane;
    @FXML
    Pane gameBackground;
    @FXML
    StackPane helpWindowContainer;
    @FXML
    StackPane hintCaptainContainer;
    @FXML
    Pane shadow;
    @FXML
    StackPane eventContainer;
    @FXML
    public HBox contextMenuButtons;

    @FXML
    public StackPane contextMenuContainer;
    @FXML
    public Group group;
    @FXML
    public StackPane overviewContainer;
    @FXML
    public ScrollPane mapScrollPane;
    @FXML
    public Pane mapGrid;
    @FXML
    public StackPane zoomPane;
    @FXML
    public StackPane deleteStructureWarningContainer;
    @FXML
    public StackPane siteProperties;
    @FXML
    public StackPane buildingProperties;
    @FXML
    public StackPane buildingsWindow;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    StackPane islandClaimingContainer;

    @FXML
    public StackPane clockComponentContainer;

    @Inject
    public EventService eventService;
    @Inject
    public InGameService inGameService;
    @Inject
    EmpireService empireService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public ExplanationService explanationService;

    @Inject
    public JobsService jobsService;

    @SubComponent
    @Inject
    public JobsOverviewComponent jobsOverviewComponent;
    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public IslandClaimingComponent islandClaimingComponent;
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
    public EmpireOverviewComponent empireOverviewComponent;
    @SubComponent
    @Inject
    public HelpComponent helpComponent;
    @SubComponent
    @Inject
    public CoolerBubbleComponent coolerBubbleComponent;
    @SubComponent
    @Inject
    public ClockComponent clockComponent;
    @SubComponent
    @Inject
    public TechnologyOverviewComponent technologiesComponent;
    @SubComponent
    @Inject
    public MarketComponent marketOverviewComponent;

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
    @Inject
    public VariableService variableService;
    @Inject
    public LobbyService lobbyService;

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
    PopupBuilder popupHelpWindow = new PopupBuilder();


    @OnRender
    public void addSpeechBubble() {
        hintCaptainContainer.getChildren().add(coolerBubbleComponent);
        coolerBubbleComponent.silence();
    }


    @OnInit
    public void init() {
        overviewSitesComponent.setIngameController(this);
        overviewUpgradeComponent.setIngameController(this);
        buildingsWindowComponent.setInGameController(this);
        buildingPropertiesComponent.setInGameController(this);
        sitePropertiesComponent.setInGameController(this);
        deleteStructureComponent.setInGameController(this);
        empireOverviewComponent.setInGameController(this);
        variableService.setIngameController(this);
		pauseMenuComponent.setInGameController(this);
        marketOverviewComponent.setInGameController(this);
        storageOverviewComponent.setInGameController(this);
        pauseMenuComponent.setInGameController(this);
        helpComponent.setInGameController(this);

        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();

        GameStatus gameStatus = inGameService.getGameStatus();
        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_PAUSED"));

        variableService.initVariables();

        this.subscriber.subscribe(this.lobbyService.getMember(gameID, tokenStorage.getUserId()),
                result -> tokenStorage.setEmpireTraits(result.empire().traits()));

        this.subscriber.subscribe(this.inGameService.getVariablesEffects(),
                result -> variableService.setVariablesEffect(result));

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

    /*
      This method should be called every time after a job is done.
    */
    public void updateVariableDependencies() {
        variableService.loadVariablesDataStructure();
        loadGameAttributes();
    }

    public void loadGameAttributes() {
        islandAttributes.setSystemUpgradeAttributes();
        islandAttributes.setBuildingAttributes();
        islandAttributes.setDistrictAttributes();
    }

    private void handlePauseChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            pause = (Boolean) propertyChangeEvent.getNewValue();
            if (pause) {
                shadow.setVisible(true);
                shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
                eventContainer.toFront();
                eventComponent.toFront();
                pauseGame();
            } else {
                if (!eventContainer.isVisible()) {
                    shadow.setVisible(false);
                }
                resumeGame();
            }
        }
    }

    @OnRender
    public void render() {
        buildingProperties.setMouseTransparent(true);
        buildingProperties.setPickOnBounds(false);
        buildingsWindow.setMouseTransparent(true);
        siteProperties.setMouseTransparent(true);
        siteProperties.setPickOnBounds(false);
        deleteStructureWarningContainer.setMouseTransparent(true);
        helpWindowContainer.setMouseTransparent(true);
        helpComponent.setVisible(false);
        helpComponent.setMouseTransparent(true);



        pauseMenuContainer.setMouseTransparent(true);
        pauseMenuContainer.setVisible(false);
        eventComponent.setParent(shadow, eventContainer);
        clockComponent.setToggle(true);
        clockComponentContainer.getChildren().add(clockComponent);
        eventContainer.getChildren().add(eventComponent);
        eventContainer.setVisible(false);
        shadow.setVisible(false);
        eventComponent.setClockComponent(clockComponent);
        eventComponent.setBackground(gameBackground);

        pauseMenuContainer.getChildren().add(pauseMenuComponent);

        overviewContainer.setVisible(false);
        overviewSitesComponent.setContainer();
        overviewContainer.getChildren().add(overviewSitesComponent);
        overviewContainer.getChildren().add(overviewUpgradeComponent);
        islandClaimingContainer.getChildren().add(this.islandClaimingComponent);
        islandClaimingContainer.setVisible(false);

        technologiesComponent.setContainer(contextMenuContainer);

        contextMenuContainer.setPickOnBounds(false);
        contextMenuContainer.getChildren().addAll(
                storageOverviewComponent,
                jobsOverviewComponent,
                empireOverviewComponent,
                technologiesComponent,
                marketOverviewComponent
        );
        contextMenuContainer.getChildren().forEach(child -> child.setVisible(false));

        this.createContextMenuButtons();

  		this.jobsService.loadEmpireJobs();
        this.jobsService.initializeJobsListeners();
        explanationService.setInGameController(this);
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        helpComponent.setVisible(false);
        helpWindowContainer.setVisible(false);
        helpComponent.setMouseTransparent(true);
        helpWindowContainer.setMouseTransparent(true);

        pause = !pause;
        inGameService.setShowSettings(false);
        inGameService.setPaused(pause);
        if (pause) {
            shadow.setVisible(true);
            shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
            eventContainer.toFront();
            eventComponent.toFront();
            pauseMenuContainer.setMouseTransparent(false);
            pauseGame();
        } else {
            pauseMenuContainer.setMouseTransparent(true);
            resumeGame();
        }
    }


    @OnKey(code = KeyCode.J, alt = true)
    public void showJobsOverview() {
        this.toggleContextMenuVisibility(this.jobsOverviewComponent);
        this.jobsOverviewComponent.setVisible(!this.jobsOverviewComponent.isVisible());
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void showStorageOverview() {
        this.toggleContextMenuVisibility(this.storageOverviewComponent);
        this.storageOverviewComponent.setVisible(!this.storageOverviewComponent.isVisible());
    }

    @OnKey(code = KeyCode.M, alt = true)
    public void showMarket() {
        this.toggleContextMenuVisibility(this.marketOverviewComponent);
        this.marketOverviewComponent.setVisible(!this.marketOverviewComponent.isVisible());
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void showHelpOverview() {
        if (this.helpComponent.isVisible()) {
            this.helpComponent.close();
            this.removePause();
        } else showHelp();
    }

    private void toggleContextMenuVisibility(Node node) {
        this.contextMenuContainer.getChildren().stream()
                .filter(child -> !child.equals(node))
                .forEach(child -> child.setVisible(false));
    }

    public void pauseGame() {
        closeComponents();
        pauseMenuContainer.toFront();
        pauseMenuComponent.toFront();
        pauseMenuComponent.setVisible(true);
        pauseMenuContainer.setVisible(pause);
        pauseMenuContainer.setMouseTransparent(false);
    }

    public void pauseGameFromHelp() {
        pause = true;
        inGameService.setPaused(true);
        if (pause) {
            pauseMenuContainer.setMouseTransparent(false);
            pauseGame();
        } else {
            pauseMenuContainer.setMouseTransparent(true);
            resumeGame();
        }
    }

    public void closeComponents() {
        this.contextMenuContainer.getChildren().forEach(child -> child.setVisible(false));
    }

    public void resumeGame() {
        pauseMenuContainer.setVisible(pause);
    }

    public void removePause() {
        pause = false;
        pauseMenuContainer.setVisible(false);
        shadow.setVisible(false);
    }

    /**
     * Please read the {@link ContextMenuButton ContextMenuButton} documentation to add additional context menu nodes.
     */
    private void createContextMenuButtons() {
        if (!tokenStorage.isSpectator())
            this.contextMenuButtons.getChildren().addAll(
                    new ContextMenuButton("storageOverview", this.storageOverviewComponent),
                    new ContextMenuButton("empireOverview", this.empireOverviewComponent),
                    new ContextMenuButton("jobsOverview", this.jobsOverviewComponent),
                    new ContextMenuButton("technologies", this.technologiesComponent),
                    new ContextMenuButton("marketOverview", this.marketOverviewComponent)
            );
    }

    @OnRender
    public void createMap() {
        this.islandComponentList = islandsService.createIslands(islandsService.getListOfIslands());
        this.islandComponentMap = islandsService.getComponentMap();
        mapGrid.setMinSize(islandsService.getMapWidth(), islandsService.getMapHeight());
        islandsService.createLines(this.islandComponentMap).forEach(line -> this.mapGrid.getChildren().add(line));

        group.setScaleX(0.65);
        group.setScaleY(0.65);

        // Event Listener for Island changes
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.systems.%s.updated",
                        tokenStorage.getGameId(), "*"), SystemDto.class),
                event -> {
                    IslandComponent isle = islandsService.getIslandComponent(event.data()._id());
                    Island updatedIsland = islandsService.convertToIsland(event.data());
                    isle.applyInfo(updatedIsland);
                    if (Objects.nonNull(updatedIsland.owner())) {
                        // apply drop shadow and flag
                        isle.applyEmpireInfo();
                        // island is already claimed
                        this.islandClaimingContainer.setVisible(false);
                    }
                    // check if the island/upgrade overview is visible for the updated island
                    if (Objects.nonNull(selectedIsland) &&
                            updatedIsland.id().equals(selectedIsland.island.id()) &&
                            (overviewSitesComponent.isVisible() || overviewUpgradeComponent.isVisible())) {
                        islandAttributes.setIsland(updatedIsland);
                        String shownPage = overviewSitesComponent.getShownPage();
                        // open the island overview again with updated information
                        showOverview();
                        switch (shownPage) {
                            case "upgrade" -> overviewSitesComponent.showUpgrades();
                            case "details" -> overviewSitesComponent.showDetails();
                            case "buildings" -> overviewSitesComponent.showBuildings();
                            case "sites" -> overviewSitesComponent.showSites();
                            case "jobs" -> overviewSitesComponent.showJobs();
                        }
                    }
                },
                error -> System.out.println("islands event listener error: " + error)
        );

        this.islandComponentList.forEach(isle -> {
            isle.setInGameController(this);
            isle.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showInfo);
            isle.setScaleX(1.25);
            isle.setScaleY(1.25);
            this.mapGrid.getChildren().add(isle);
        });

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
                scale = Math.min(scale, 3);
                event.consume();
            } else if (event.isShiftDown() && (event.getDeltaY() < 0 || event.getDeltaX() < 0)) {
                scale -= 0.1;
                scale = Math.max(scale, 0.35);
                event.consume();
            }
            group.setScaleX(scale);
            group.setScaleY(scale);
        });

    }

    public void showInfo(MouseEvent event) {
        if (event.getSource() instanceof IslandComponent selected) {
            tokenStorage.setIsland(selected.getIsland());
            selectedIsland = selected;
            tokenStorage.setIsland(selectedIsland.getIsland());
            islandAttributes.setIsland(selectedIsland.getIsland());
            if (Objects.nonNull(selected.getIsland().owner())) {
                this.islandClaimingContainer.setVisible(false);
                this.sitePropertiesComponent.setVisible(false);
                this.buildingPropertiesComponent.setVisible(false);
                if (selected.island.owner().equals(this.tokenStorage.getEmpireId()))
                    this.overviewSitesComponent.jobsComponent.setJobsObservableList(
                        this.jobsService.getObservableListForSystem(this.tokenStorage.getIsland().id()));
                showOverview();
                selected.showUnshowRudder();
            } else {
                if (Objects.nonNull(selectedIsland)) selectedIsland.showUnshowRudder();
                this.overviewSitesComponent.closeOverview();
                if (this.islandClaimingContainer.getLayoutX()+80 == selected.getLayoutX() &&
                        this.islandClaimingContainer.getLayoutY()+220 == selected.getLayoutY() &&
                        this.islandClaimingContainer.isVisible()) {
                    this.islandClaimingContainer.setVisible(false);
                } else {
                    this.islandClaimingContainer.setVisible(true);
                    this.islandClaimingContainer.setLayoutX(selected.getLayoutX()-80);
                    this.islandClaimingContainer.setLayoutY(selected.getLayoutY()-220);
                    this.islandClaimingComponent.setIslandInformation(selected.island);
                }
            }
        }
    }

    @OnRender
    public void setJobInspectors() {
        this.jobsService.setJobInspector("island_jobs_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.tokenStorage.setIsland(selected);
            this.overviewSitesComponent.jobsComponent.setJobsObservableList(
                    this.jobsService.getObservableListForSystem(job.system()));

            this.islandAttributes.setIsland(selected);
            selectedIsland = this.islandsService.getIslandComponent(job.system());
            if (Objects.nonNull(selected.owner())) {
                showOverview();
                this.overviewSitesComponent.showJobs();
            }
        });

        this.jobsService.setJobInspector("island_upgrade", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.overviewSitesComponent.showUpgrades();
        });

        this.jobsService.setJobInspector("site_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.setSiteType(job.district());
            this.showSiteOverview();
        });

        this.jobsService.setJobInspector("building_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.showBuildingInformation(job.building(), job._id(), BUILT_STATUS.QUEUED);
        });

        this.jobsService.setJobInspector("building_done_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            // after the job is done, the isBuilt should be true cause the building is built!
            this.showBuildingInformation(job.building(), "", BUILT_STATUS.BUILT);
        });

        this.jobsService.setJobInspector("storage_overview", (Jobs.Job job) -> showStorageOverview());

//        this.jobsService.setJobInspector("technology_overview", (Jobs.Job job) ->
//                showStorageOverview()
//        );

    }

    public void showOverview() {
            overviewSitesComponent.buildingsComponent.resetPage();
            overviewSitesComponent.buildingsComponent.setGridPane();
            overviewContainer.setVisible(true);
            overviewSitesComponent.sitesContainer.setVisible(true);
            overviewSitesComponent.buildingsButton.setDisable(true);
            inGameService.showOnly(overviewContainer, overviewSitesComponent);
            inGameService.showOnly(overviewSitesComponent.sitesContainer, overviewSitesComponent.buildingsComponent);
            overviewSitesComponent.setOverviewSites();
            // update island name
            if (!this.islandAttributes.getIsland().name().isEmpty())
                overviewSitesComponent.inputIslandName.setText(this.islandAttributes.getIsland().name());
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void showStorage() {
        if(empireOverviewComponent.isVisible()) {
            empireOverviewComponent.closeEmpireOverview();
        }
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void showEmpireOverview() {
        if(storageOverviewComponent.isVisible()){
            storageOverviewComponent.closeStorageOverview();
        }
    }

    @OnKey(code = KeyCode.SPACE)
    public void resetZoom() {
        scale = 0.65;
        group.setScaleX(scale);
        group.setScaleY(scale);
    }

    public void resetZoomMouse(@NotNull MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            resetZoom();
        }
    }

    public void showBuildingInformation(String buildingToAdd, String jobID, BUILT_STATUS isBuilt) {
        System.out.println("built " + isBuilt);
        siteProperties.setVisible(false);
        siteProperties.setMouseTransparent(true);
        buildingPropertiesComponent.setBuildingType(buildingToAdd, jobID, isBuilt);
        popupBuildingProperties.showPopup(buildingProperties, buildingPropertiesComponent);
    }

    public void handleDeleteStructure(String buildingType) {
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
    }

    public void createEmpireListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    if (!lastUpdate.equals(event.data().updatedAt())) {
                        islandAttributes.setEmpireDto(event.data());
                        overviewUpgradeComponent.setUpgradeButton();
                        this.lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorListener: " + error)
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

    public void setSitePropertiesInvisible() {
        sitePropertiesComponent.setVisible(false);
        buildingProperties.setMouseTransparent(false);
        overviewSitesComponent.buildingsComponent.setGridPane();
    }

    public void showHelp() {
        popupHelpWindow.showPopup(helpWindowContainer,helpComponent);
        helpComponent.setVisible(true);
        helpComponent.setMouseTransparent(false);
        helpWindowContainer.setMouseTransparent(false);
        helpWindowContainer.toFront();
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.setMouseTransparent(true);
        helpComponent.displayTechnologies();
    }

    private void removeIslands() {
        // removes islands from map
        islandComponentList.forEach(IslandComponent::destroy);
        islandComponentList = null;
        islandComponentMap = null;
        islandsService.removeDataForMap();
    }

    @OnDestroy
    public void destroy() {
        removeIslands();
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
                .removePropertyChangeListener(triple.propertyName(), triple.listener()));
        this.subscriber.dispose();
        this.jobsService.dispose();
        this.variableService.dispose();
    }
}
