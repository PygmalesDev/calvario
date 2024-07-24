package de.uniks.stp24.controllers;

import de.uniks.stp24.component.dev.FleetCreationComponent;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.technology.TechnologyOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.helper.Draggable;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.Constants;
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
import java.util.*;

import static de.uniks.stp24.service.Constants.BUILT_STATUS;

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
    public Pane shadow;
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
    @Inject
    public FleetService fleetService;
    @Inject
    public FleetCoordinationService fleetCoordinationService;

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
    public FleetCreationComponent fleetCreationComponent;

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
    public final ArrayList<String> flagsPath = new ArrayList<>();
    final String resourcesPaths = "/de/uniks/stp24/assets/";
    final String flagsFolderPath = "flags/flag_";
    final PopupBuilder popupBuildingProperties = new PopupBuilder();
    final PopupBuilder popupBuildingWindow = new PopupBuilder();
    final PopupBuilder popupSiteProperties = new PopupBuilder();
    final PopupBuilder popupDeleteStructure = new PopupBuilder();
    final PopupBuilder popupHelpWindow = new PopupBuilder();

    final ArrayList<Node> draggables = new ArrayList<>();

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
        pauseMenuComponent.setInGameController(this);
        helpComponent.setInGameController(this);
        clockComponent.setInGameController(this);
        fleetCoordinationService.setInGameController(this);

        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();

        System.out.println("GAME ID " + gameID);
        System.out.println("EMPIRE ID " + empireID);

        GameStatus gameStatus = inGameService.getGameStatus();
        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_PAUSED"));

        variableService.initVariables();
        variableService.addRunnable(this::loadGameAttributes);

        this.fleetCoordinationService.setInitialFleetPosition();

        if (!tokenStorage.isSpectator()) {
            this.subscriber.subscribe(empireService.getEmpire(gameID, empireID),
                    result -> islandAttributes.setEmpireDto(result),
                    error -> System.out.println("error in getEmpire in inGame"));
            createEmpireListener();
        }

        for (int i = 0; i <= 16; i++) this.flagsPath.add(resourcesPaths + flagsFolderPath + i + ".png");
    }

    /*
      This method should be called every time after a job is done.
    */
    public void updateVariableDependencies() {
        variableService.loadVariablesDataStructure();
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
                pauseGame();
            } else {
                if (!eventContainer.isVisible()) shadow.setVisible(false);
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

        this.group.getChildren().add(this.fleetCreationComponent);
        this.fleetCreationComponent.setVisible(false);

        contextMenuContainer.setPickOnBounds(false);
        contextMenuContainer.getChildren().addAll(
                storageOverviewComponent,
                jobsOverviewComponent,
                empireOverviewComponent,
                technologiesComponent,
                marketOverviewComponent
        );
        contextMenuContainer.getChildren().forEach(child -> {
            child.setVisible(false);
            // make every node in contextMenuContainer draggable
            draggables.add(child);
        });
        this.createContextMenuButtons();

        // make pop ups draggable
        draggables.add(eventContainer);
        draggables.add(deleteStructureWarningContainer);

        draggables.forEach(Draggable.DraggableNode::new);

        // make island overview draggable (the nodes attached to the overview will follow it)
        draggables.addAll(Arrays.asList(overviewContainer, buildingsWindow, buildingProperties, siteProperties));
        new Draggable.DraggableNode(overviewContainer, buildingsWindow, buildingProperties, siteProperties);

        this.jobsService.loadEmpireJobs();
        this.jobsService.initializeJobsListeners();
        explanationService.setInGameController(this);

        this.fleetService.loadGameFleets();
        this.fleetService.initializeFleetListeners();

        this.mapGrid.setOnMouseClicked(this.fleetCoordinationService::travelToMousePosition);
    }

    @OnKey(code = KeyCode.R, alt = true)
    public void resetDraggables() {
        for (Node draggable : draggables) {
            draggable.setTranslateX(0);
            draggable.setTranslateY(0);
        }
    }


    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        helpComponent.setVisible(false);
        helpWindowContainer.setVisible(false);
        helpComponent.setMouseTransparent(true);
        helpWindowContainer.setMouseTransparent(true);

        pause = !pause;
        this.inGameService.gameStatus.firePropertyChange(GameStatus.PROPERTY_PAUSED, null, null);
        inGameService.setShowSettings(false);
        inGameService.setPaused(pause);
        if (pause) {
            shadow.setVisible(true);
            shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
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
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void showStorageOverview() {
        this.toggleContextMenuVisibility(this.storageOverviewComponent);
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void showEmpireOverview() {
        this.toggleContextMenuVisibility(this.empireOverviewComponent);
    }

    @OnKey(code = KeyCode.M, alt = true)
    public void showMarket() {
        this.toggleContextMenuVisibility(this.marketOverviewComponent);
    }

    @OnKey(code = KeyCode.T, alt = true)
    public void showTechnologiesOverview() {
        this.toggleContextMenuVisibility(this.technologiesComponent);
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void showHelpOverview() {
        if (this.helpComponent.isVisible()) {
            this.helpComponent.close();
            shadow.setVisible(false);
            this.removePause();
        } else showHelp();
    }

    private void toggleContextMenuVisibility(Node node) {
        if (!tokenStorage.isSpectator()) {
            this.contextMenuContainer.getChildren().stream()
                    .filter(child -> !child.equals(node))
                    .forEach(child -> child.setVisible(false));
            node.setVisible(!node.isVisible());
        }
    }

    public void pauseGame() {
        closeComponents();
        pauseMenuComponent.setVisible(true);
        pauseMenuContainer.setVisible(pause);
        pauseMenuContainer.setMouseTransparent(false);
    }

    public void pauseGameFromHelp() {
        pause = true;
        inGameService.setPaused(true);
        if (pause) {
            pauseMenuContainer.setMouseTransparent(false);
            shadow.setVisible(true);
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
            isle.collisionCircle.setRadius(Constants.ISLAND_COLLISION_RADIUS);
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
            this.fleetCoordinationService.getTravelPath(selected);

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

                // Show island claiming scroll
            } else if (!this.tokenStorage.isSpectator()) {
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
            // Show fleet creation pane
            this.fleetCreationComponent.setVisible(true);
            this.fleetCreationComponent.setIsland(selected.island.id());
            this.fleetCreationComponent.setLayoutX(selected.getLayoutX()-100);
            this.fleetCreationComponent.setLayoutY(selected.getLayoutY()+30);
        }
    }

    public void setFleetOnMap(GameFleetController fleet) {
        this.mapGrid.getChildren().add(fleet);
    }

    @OnRender
    public void setJobInspectors() {
        this.jobsService.setJobInspector("island_jobs_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            if (selected.upgrade().equals("unexplored") || selected.upgrade().equals("explored")) return;

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
            // after the job is done, the isBuilt should be BUILT cause the building is built!
            this.showBuildingInformation(job.building(), "", BUILT_STATUS.BUILT);
        });

        this.jobsService.setJobInspector("storage_overview", (Jobs.Job job) -> showStorageOverview());

        this.jobsService.setJobInspector("technology_overview", (Jobs.Job job) ->
                showTechnologiesOverview()
        );

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

    @OnKey(code = KeyCode.SPACE)
    public void resetZoom() {
        scale = 0.65;
        group.setScaleX(scale);
        group.setScaleY(scale);
    }

    @OnKey(code = KeyCode.R, control = true)
    public void setCollisionVisibility() {
        this.mapGrid.getChildren().forEach(child -> {
            if (child instanceof IslandComponent island) island.collisionCircle.setVisible(!island.collisionCircle.isVisible());
            if (child instanceof GameFleetController fleet) fleet.collisionCircle.setVisible(!fleet.collisionCircle.isVisible());
        });

    }

    public void resetZoomMouse(@NotNull MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            resetZoom();
        }
    }

    public void showBuildingInformation(String buildingToAdd, String jobID, BUILT_STATUS isBuilt) {
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
        shadow.setVisible(true);
        helpComponent.setMouseTransparent(false);
        helpWindowContainer.setMouseTransparent(false);
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
        this.fleetService.dispose();
        this.variableService.dispose();
    }
}
