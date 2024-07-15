package de.uniks.stp24;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.component.game.technology.*;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class AppTest2 extends ControllerTest {
    @InjectMocks
    InGameController inGameController;
    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    EventComponent eventComponent;
    @InjectMocks
    ClockComponent clockComponent;
    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    SitesComponent sitesComponent;
    @InjectMocks
    DetailsComponent detailsComponent;
    @InjectMocks
    BuildingsComponent buildingsComponent;
    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;
    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;
    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;
    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;
    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;
    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;
    @InjectMocks
    IslandOverviewJobsComponent islandOverviewJobsComponent;
    @InjectMocks
    EmpireOverviewComponent empireOverviewComponent;
    @InjectMocks
    PropertiesJobProgressComponent propertiesJobProgressComponent;
    @InjectMocks
    HelpComponent helpComponent;
    @InjectMocks
    IslandClaimingComponent islandClaimingComponent;

    @InjectMocks
    VariableExplanationComponent variableExplanationComponent;

    @InjectMocks
    TechnologyOverviewComponent technologiesComponent;

    @InjectMocks
    TechnologyCategoryComponent technologyCategoryComponent;
    @InjectMocks
    ResearchJobComponent researchJobComponent;

    @Spy
    ResourceBundle technologiesResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/technologies", Locale.ROOT);
    @Spy
    JobsService jobsService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    TimerService timerService;
    @Spy
    GamesApiService gameApiService;
    @Spy
    EmpireService empireService;
    @Spy
    InfrastructureService infrastructureService;

    @Spy
    GameStatus gameStatus;

    @Spy
    InGameService inGameService;
    @Spy
    ImageCache imageCache;
    @Spy
    EventService eventService;

    @Spy
    VariableService variableService;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    Subscriber subscriber = spy(Subscriber.class);

    @Spy
    LanguageService languageService;
    @Spy
    JobsApiService jobsApiService;

    @Spy
    ResourcesService resourcesService;
    @Spy
    GameSystemsApiService gameSystemsApiService;
    @Spy
    EmpireApiService empireApiService;

    @Spy
    TechnologyService technologyService;
    @Spy
    IslandComponent islandComponent = spy(IslandComponent.class);

    Map<String, Integer> cost = Map.of("energy", 3, "fuel", 2);
    Map<String, Integer> upkeep = Map.of("energy", 3, "fuel", 8);
    UpgradeStatus unexplored = new UpgradeStatus("unexplored", 1, cost, upkeep, 1);
    UpgradeStatus explored = new UpgradeStatus("explored", 1, cost, upkeep, 1);
    UpgradeStatus colonized = new UpgradeStatus("colonized", 1, cost, upkeep, 1);
    UpgradeStatus upgraded = new UpgradeStatus("upgraded", 1, cost, upkeep, 1);
    UpgradeStatus developed = new UpgradeStatus("developed", 1, cost, upkeep, 1);

    SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);
    ArrayList<BuildingPresets> buildingPresets = new ArrayList<>();
    ArrayList<DistrictPresets> districtPresets = new ArrayList<>();
    SystemDto[] systems = new SystemDto[3];
    List<IslandComponent> testIsleComps;
    Button[] buttons = new Button[3] ;
    Line[] linesR = new Line[2] ;
    Island island1;
    Map<String,InfrastructureService> testMapInfra = new HashMap<>();


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = eventComponent;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.variableExplanationComponent = this.variableExplanationComponent;
        this.inGameController.technologiesComponent = this.technologiesComponent;
        this.technologiesComponent.technologyCategoryComponent = technologyCategoryComponent;
        this.technologyCategoryComponent.researchJobComponent = researchJobComponent;

        this.clockComponent.timerService = this.timerService;
        this.clockComponent.eventService = this.eventService;
        this.clockComponent.subscriber = this.subscriber;
        this.clockComponent.gamesApiService = this.gameApiService;
        this.clockComponent.islandsService = this.islandsService;
        this.clockComponent.eventComponent = this.eventComponent;
        this.eventComponent.empireApiService = this.empireApiService;
        this.islandsService.app = this.app;
        this.islandAttributeStorage.systemPresets = systemUpgrades;
        inGameService.setGameStatus(gameStatus);
        islandsService.gameSystemsService = this.gameSystemsApiService;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.overviewUpgradeComponent= this.overviewUpgradeComponent;
        this.inGameController.helpComponent = this.helpComponent;

        this.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.timerService.tokenStorage = this.tokenStorage;
        this.timerService.subscriber = this.subscriber;
        this.timerService.gamesApiService = this.gameApiService;
        this.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;
        this.sitePropertiesComponent.siteJobProgress = this.propertiesJobProgressComponent;

        inGameController.mapScrollPane = new ScrollPane();
        inGameController.group = new Group();
        inGameController.zoomPane = new StackPane();
        inGameController.mapGrid = new Pane();

        this.jobsService.tokenStorage = this.tokenStorage;
        this.jobsService.jobsApiService = this.jobsApiService;
        this.jobsService.subscriber = this.subscriber;
        this.jobsService.eventListener =this.eventListener;

        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.islandClaimingComponent.jobsService = this.jobsService;
        this.islandClaimingComponent.islandAttributes = this.islandAttributeStorage;
        this.islandClaimingComponent.imageCache = this.imageCache;
        this.islandClaimingComponent.islandsService = this.islandsService;

        inGameController.zoomPane.getChildren().add(inGameController.mapGrid);
        inGameController.group.getChildren().add(inGameController.zoomPane);
        inGameController.mapScrollPane.setContent(inGameController.group);


        doReturn(Observable.empty()).when(this.jobsApiService).getEmpireJobs(any(), any());
        doNothing().when(variableService).initVariables();

        doReturn(gameStatus).when(this.inGameService).getGameStatus();
        doReturn(Observable
                .just(new Game("a", null, "game1Id", "testGame1",
                        "testHost1", 2, 0, true, 1,10, null))).when(gameApiService).getGame(any());
        doReturn(null).when(this.app).show("/ingame");
        islandsService.saveEmpire("empire",new ReadEmpireDto("a","b","empire","game1","user1","name",
                "description","#FFDDEE",2,3,"home"));
//        SystemDto[] systems = new SystemDto[3];
        ArrayList<String> buildings = new ArrayList<>(Arrays.asList("power_plant", "mine", "farm", "research_lab", "foundry", "factory", "refinery"));
        systems[0] = new SystemDto("a","b","system1","game1","agriculture",
                "name",null,null,25,null, Upgrade.unexplored,0,
                Map.of("home",22),1.46,-20.88,null);
        systems[1] = new SystemDto("a","b","system2","game1","energy",
                "name",null,
          Map.of("city",2, "industry", 3, "mining",4, "energy",5, "agriculture",6),
          26,buildings, Upgrade.unexplored,0,
                Map.of("home",18),-7.83,-11.04,"empire");
        systems[2] = new SystemDto("a","b","home","game1","uninhabitable_0", "name",
                Map.of("city",2, "industry", 3, "mining",4, "energy",5, "agriculture",6),
                Map.of("city",2, "industry", 2, "mining",3, "energy",4, "agriculture",6), 22,
                buildings,Upgrade.developed,25,Map.of("system1",22,"system2",18),-5.23,4.23,"empire"
        );

        IslandComponent comp0 = new IslandComponent();
        IslandComponent comp1 = new IslandComponent();
        IslandComponent comp2 = new IslandComponent();
        Map<String, IslandComponent> compMap = Map.of("system1", comp0,
                "system2", comp1,
                "home" , comp2);
        List<IslandComponent> compList = Arrays.asList(comp0,comp1,comp2);
        doReturn(Observable.just(systems)).when(gameSystemsApiService).getSystems(any());
        doReturn(compMap).when(islandsService).getComponentMap();

        doReturn(Observable.just(buildingPresets)).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(districtPresets)).when(inGameService).loadDistrictPresets();
        doReturn(Observable.just(systemUpgrades)).when(inGameService).loadUpgradePresets();

        Mockito.doCallRealMethod().when(islandsService).retrieveIslands(any());
        Mockito.doCallRealMethod().when(islandsService).getListOfIslands();
        Mockito.doCallRealMethod().when(islandsService).getMapWidth();
        Mockito.doCallRealMethod().when(islandsService).getMapHeight();
        Mockito.doCallRealMethod().when(islandsService).getEmpire(any());
        Mockito.doCallRealMethod().when(islandsService).createIslands(any());
        Mockito.doCallRealMethod().when(islandsService).createIslandPaneFromDto(any(),any());
        doCallRealMethod().when(islandComponent).setPosition(anyDouble(),anyDouble());

        doReturn(null).when(imageCache).get(any());

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, new HashMap<>() {{put("energy", 5);put("population", 4);}},
                null))).when(this.empireService).getEmpire(any(),any());
        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        doReturn(Observable.just(new EffectSourceParentDto(new EffectSourceDto[]{}))).when(empireApiService).getEmpireEffect(any(), any());

        app.show(inGameController);
        eventComponent.getStylesheets().clear();
        helpComponent.getStylesheets().clear();
        storageOverviewComponent.getStylesheets().clear();
        clockComponent.getStylesheets().clear();
        pauseMenuComponent.getStylesheets().clear();
        overviewSitesComponent.getStylesheets().clear();
        overviewUpgradeComponent.getStylesheets().clear();
        sitesComponent.getStylesheets().clear();
        jobsOverviewComponent.getStylesheets().clear();
        islandClaimingComponent.getStylesheets().clear();

        island1 = new Island(
          "testEmpireID",
          1,
          50,
          50,
          IslandType. valueOf("uninhabitable_0"),
          20,
          25,
          2,
          Map.of("energy", 3, "agriculture" , 6),
          Map.of("energy", 3, "agriculture" , 6),
          buildings,
          "1",
          "developed",
                "TestIsland1"
        );

    }

    @Test
    public void createIslandData(){
        assertEquals(0,islandsService.getListOfIslands().size());
        islandsService.retrieveIslands("game1");
        gameSystemsApiService.getSystems("game1");
        WaitForAsyncUtils.waitForFxEvents();
        List<Island> testIsles = islandsService.getListOfIslands();

        testIsleComps = islandsService.createIslands(testIsles);
        Map<String,IslandComponent> testIsleMap = islandsService.getComponentMap();
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(3,testIsles.size());
        assertEquals(3,testIsleComps.size());
        assertEquals(3,testIsleMap.size());
        List<Line> lines = islandsService.createLines(testIsleMap);
        assertEquals(2,lines.size());
        assertNotNull(islandsService.getEmpire("empire"));
        assertNotEquals(0,islandsService.getMapWidth());
        assertNotEquals(0,islandsService.getMapHeight());
        assertEquals(2,islandsService.getSiteManagerSize());

        Platform.runLater(() ->{
            createIcons();
            createLines();
            waitForFxEvents();
            inGameController.mapGrid.setMinSize(1000,600);
            inGameController.mapGrid.getChildren().addAll(linesR[0],linesR[1]);
            inGameController.mapGrid.getChildren().add(buttons[0]);
            inGameController.mapGrid.getChildren().add(buttons[1]);
            inGameController.mapGrid.getChildren().add(buttons[2]);
            waitForFxEvents();
        });

        waitForFxEvents();
        clickOn("#storageOverviewButton");
        waitForFxEvents();

        assertTrue(inGameController.storageOverviewComponent.isVisible());

        waitForFxEvents();
        clickOn("#storageOverviewButton");
        waitForFxEvents();

        assertEquals(37,islandsService.getAllNumberOfSites("empire"));
        assertEquals(17,islandsService.getCapacityOfOneSystem("home"));
        assertEquals(9,islandsService.getNumberOfSites("empire","energy"));

        openStorage();

    }

    public void createIcons(){
        buttons[0] = new Button();
        buttons[0].setLayoutX(200);
        buttons[0].setLayoutY(200);
        buttons[0].setPrefWidth(50);
        buttons[0].setPrefHeight(50);
        buttons[0].setId("comp0");
        buttons[0].setStyle("-fx-background-image: url('/de/uniks/stp24/icons/islands/uninhabitable_0.png')");

        buttons[1] = new Button();
        buttons[1].setLayoutX(400);
        buttons[1].setLayoutY(200);
        buttons[1].setPrefWidth(50);
        buttons[1].setPrefHeight(50);
        buttons[1].setId("comp1");
        buttons[1].setStyle("-fx-background-image: url('/de/uniks/stp24/icons/islands/uninhabitable_0.png')");

        buttons[2] = new Button();
        buttons[2].setLayoutX(200);
        buttons[2].setLayoutY(400);
        buttons[2].setPrefWidth(50);
        buttons[2].setPrefHeight(50);
        buttons[2].setId("comp2");
        buttons[2].setStyle("-fx-background-image: url('/de/uniks/stp24/icons/islands/uninhabitable_0.png')");

        buttons[0].setOnAction(event -> System.out.println("food"));
        buttons[1].setOnAction(event -> System.out.println("food"));
        buttons[2].setOnAction(event -> inGameController.showStorageOverview());
    }

    public void createLines(){
        linesR[0] = new Line(buttons[0].getLayoutX()+25,
          buttons[0].getLayoutY()+25,
          buttons[2].getLayoutX()+25,
          buttons[2].getLayoutY()+25
          ) ;
        linesR[0].setStrokeWidth(2);
        linesR[1] = new Line(buttons[1].getLayoutX()+25,
          buttons[1].getLayoutY()+25,
          buttons[2].getLayoutX()+25,
          buttons[2].getLayoutY()+25
        ) ;
        linesR[1].setStrokeWidth(2);

    }


    public void openStorage(){
        waitForFxEvents();
        // Storage is closed
        assertFalse(this.inGameController.storageOverviewComponent.isVisible());
        // Open storage
        clickOn("#storageOverviewButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewComponent.isVisible());
        // Close storage with Button in Ingame
        clickOn("#storageOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewComponent.isVisible());
        // Open again
        clickOn("#storageOverviewButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewComponent.isVisible());
        // Close storage with button in StorageOverviewComponent
        clickOn("#closeStorageOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewComponent.isVisible());
    }

}