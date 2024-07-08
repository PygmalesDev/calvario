package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
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
import de.uniks.stp24.service.PopupBuilder;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
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
    StackPane helpWindowContainer;
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
    StackPane deleteStructureWarningContainer;
    @FXML
    public StackPane siteProperties;
    @FXML
    StackPane buildingProperties;
    @FXML
    StackPane buildingsWindow;
    @FXML
    StackPane pauseMenuContainer;

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
    JobsService jobsService;

    @SubComponent
    @Inject
    public JobsOverviewComponent jobsOverviewComponent;
    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;

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
    public ClockComponent clockComponent;

    @SubComponent
    @Inject
    public DeleteStructureComponent deleteStructureComponent;
    @SubComponent
    @Inject
    public EventComponent eventComponent;

    List<IslandComponent> islandComponentList;
    Map<String, IslandComponent> islandComponentMap;

    @Inject
    public Subscriber subscriber;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    EventListener eventListener;

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

    @SubComponent
    @Inject
    public BuildingPropertiesComponent buildingPropertiesComponent;

    @SubComponent
    @Inject
    public BuildingsWindowComponent buildingsWindowComponent;

    @SubComponent
    @Inject
    public SitePropertiesComponent sitePropertiesComponent;

    @SubComponent
    @Inject
    public HelpComponent helpComponent;

    PopupBuilder popupBuildingProperties = new PopupBuilder();
    PopupBuilder popupBuildingWindow = new PopupBuilder();
    PopupBuilder popupSiteProperties = new PopupBuilder();
    PopupBuilder popupDeleteStructure = new PopupBuilder();

    PopupBuilder popupHelpWindow = new PopupBuilder();

    @Inject
    public InGameController() {
        lastUpdate = "";
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
        pauseMenuComponent.setInGameController(this);
        helpComponent.setInGameController(this);

        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();

        GameStatus gameStatus = inGameService.getGameStatus();
        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_PAUSED"));

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

    private void handlePauseChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            pause = (Boolean) propertyChangeEvent.getNewValue();
            if (pause) {
                shadow.setVisible(true);
                shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
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

        contextMenuContainer.setPickOnBounds(false);
        contextMenuContainer.getChildren().addAll(
                storageOverviewComponent,
                jobsOverviewComponent,
                empireOverviewComponent
        );
        contextMenuContainer.getChildren().forEach(child -> child.setVisible(false));

        this.createContextMenuButtons();

        //Generate job stopping conditions for the already running jobs
        this.jobsService.loadEmpireJobs();
        this.jobsService.initializeJobsListener();
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        helpComponent.setVisible(false);
        helpWindowContainer.setVisible(false);
        helpComponent.setMouseTransparent(true);
        helpWindowContainer.setMouseTransparent(true);

        pause = !pause;
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

    @OnKey(code = KeyCode.I, alt = true)
    public void showIslandOverviewWindows() {
        buildingProperties.setMouseTransparent(false);
        buildingsWindow.setMouseTransparent(false);
        popupBuildingWindow.showPopup(buildingsWindow, buildingsWindowComponent);
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void showEmpireOverview() {
        this.toggleContextMenuVisibility(this.empireOverviewComponent);
        this.empireOverviewComponent.setVisible(!this.empireOverviewComponent.isVisible());
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
                    new ContextMenuButton("jobsOverview", this.jobsOverviewComponent)
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
            islandAttributes.setIsland(selected.getIsland());
            if (selected.getIsland().owner() != null) {
                selectedIsland = selected;
                if (selected.island.owner().equals(this.tokenStorage.getEmpireId()))
                    this.overviewSitesComponent.jobsComponent.setJobsObservableList(
                        this.jobsService.getObservableListForSystem(this.tokenStorage.getIsland().id()));

                showOverview();
                selected.showUnshowRudder();
            } else if (Objects.nonNull(selectedIsland)) {
                selectedIsland.showUnshowRudder();
                this.overviewSitesComponent.closeOverview();
            }
        }
    }

    @OnRender
    public void setJobInspectors() {
        this.jobsService.setJobInspector("island_jobs_overview", (String... params) -> {
            Island selected = this.islandsService.getIsland(params[0]);
            this.tokenStorage.setIsland(selected);
            this.overviewSitesComponent.jobsComponent.setJobsObservableList(
                    this.jobsService.getObservableListForSystem(params[0]));

            this.islandAttributes.setIsland(selected);
            selectedIsland = this.islandsService.getIslandComponent(params[0]);
            if (Objects.nonNull(selected.owner())) {
                showOverview();
                this.overviewSitesComponent.showJobs();
            }
        });

        this.jobsService.setJobInspector("site_overview", (String... params) -> {
            Island selected = this.islandsService.getIsland(params[1]);
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.setSiteType(params[0]);
            this.showSiteOverview();
        });

        this.jobsService.setJobInspector("building_overview", (String... params) -> {
            Island selected = this.islandsService.getIsland(params[2]);
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.showBuildingInformation(params[0], params[1]);
        });
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

    @OnKey(code = KeyCode.SPACE, alt = true)
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

    public void showBuildingInformation(String buildingToAdd, String jobID) {
        siteProperties.setVisible(false);
        siteProperties.setMouseTransparent(true);
        buildingPropertiesComponent.setBuildingType(buildingToAdd, jobID);
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

    public void updateResCapacity() {
        overviewSitesComponent.updateResCapacity();
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
        this.jobsService.dispose();
    }

}
