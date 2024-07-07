package de.uniks.stp24;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.PresetsApiService;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
    SettingsComponent settingsComponent;
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
    EmpireOverviewComponent empireOverviewComponent;
    @InjectMocks
    VariableExplanationComponent variableExplanationComponent;


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
    VariableService variableService;
    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);
    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;
    @Spy
    EventService eventService;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    GameLogicApiService gameLogicApiService;
    @Spy
    LanguageService languageService;
    @Spy
    PresetsApiService presetsApiService;
    @Spy
    ResourcesService resourcesService;
    @Spy
    GameSystemsApiService gameSystemsApiService;
    @Spy
    IslandComponent islandComponent = spy(IslandComponent.class);
    @Spy
    ExplanationService explanationService;

    Map<String, Integer> cost = Map.of("energy", 3, "fuel", 2);
    Map<String, Integer> upkeep = Map.of("energy", 3, "fuel", 8);
    UpgradeStatus unexplored = new UpgradeStatus("unexplored", null, 0,1, cost, upkeep, 1);
    UpgradeStatus explored = new UpgradeStatus("explored", null, 0,1, cost, upkeep, 1);
    UpgradeStatus colonized = new UpgradeStatus("colonized", null, 0,1, cost, upkeep, 1);
    UpgradeStatus upgraded = new UpgradeStatus("upgraded", null, 0,1, cost, upkeep, 1);
    UpgradeStatus developed = new UpgradeStatus("developed", null, 0,1, cost, upkeep, 1);

    SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);
    ArrayList<BuildingAttributes> buildingPresets = new ArrayList<>();
    ArrayList<DistrictAttributes> districtPresets = new ArrayList<>();
    SystemDto[] systems = new SystemDto[3];
    List<IslandComponent> testIsleComps;
    Button[] buttons = new Button[3] ;
    Line[] linesR = new Line[2] ;
    Island island1;
    Map<String,InfrastructureService> testMapInfra = new HashMap<>();

    Map<String, Integer> variablesPresets = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.variableService = this.variableService;
        this.inGameController.gameLogicApiService = this.gameLogicApiService;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = eventComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.variableService.inGameService = this.inGameService;
        this.clockComponent.timerService = this.timerService;
        this.clockComponent.eventService = this.eventService;
        this.clockComponent.subscriber = this.subscriber;
        this.clockComponent.gamesApiService = this.gameApiService;
        this.clockComponent.islandsService = this.islandsService;
        this.clockComponent.eventComponent = this.eventComponent;
        this.islandsService.app = this.app;
        this.islandAttributeStorage.systemUpgradeAttributes = systemUpgrades;
        inGameService.setGameStatus(gameStatus);
        islandsService.gameSystemsService = this.gameSystemsApiService;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.overviewUpgradeComponent= this.overviewUpgradeComponent;
        this.inGameService.presetsApiService = this.presetsApiService;
        inGameController.mapScrollPane = new ScrollPane();
        inGameController.group = new Group();
        inGameController.zoomPane = new StackPane();
        inGameController.mapGrid = new Pane();
        inGameController.zoomPane.getChildren().add(inGameController.mapGrid);
        inGameController.group.getChildren().add(inGameController.zoomPane);
        inGameController.mapScrollPane.setContent(inGameController.group);

        this.inGameController.variableService.subscriber = this.subscriber;
        this.inGameController.variableExplanationComponent = this.variableExplanationComponent;
        this.explanationService.app = this.app;
        this.inGameController.explanationService = this.explanationService;
        variablesPresets.put("districts.city.build_time", 9);
        variablesPresets.put("districts.city.cost.minerals", 100);
        variablesPresets.put("districts.city.upkeep.energy", 5);
        doReturn(Observable.just(variablesPresets)).when(inGameService).getVariablesPresets();

        doReturn(gameStatus).when(this.inGameService).getGameStatus();
        doReturn(Observable
                .just(new Game("a", null, "game1Id", "testGame1",
                        "testHost1", 2, true, 1,10, null))).when(gameApiService).getGame(any());
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
//        doReturn(compList).when(islandsService).createIslands(any());

        Mockito.doCallRealMethod().when(islandsService).retrieveIslands(any());
        Mockito.doCallRealMethod().when(islandsService).getListOfIslands();
        Mockito.doCallRealMethod().when(islandsService).getMapWidth();
        Mockito.doCallRealMethod().when(islandsService).getMapHeight();
        Mockito.doCallRealMethod().when(islandsService).getEmpire(any());
        Mockito.doCallRealMethod().when(islandsService).createIslands(any());
        Mockito.doCallRealMethod().when(islandsService).createIslandPaneFromDto(any(),any());
        doCallRealMethod().when(islandComponent).setPosition(anyDouble(),anyDouble());

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, new HashMap<>() {{put("energy", 5);put("population", 4);}},
                null))).when(this.empireService).getEmpire(any(),any());
        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        app.show(inGameController);
        eventComponent.getStylesheets().clear();
        storageOverviewComponent.getStylesheets().clear();
        clockComponent.getStylesheets().clear();
        pauseMenuComponent.getStylesheets().clear();
        settingsComponent.getStylesheets().clear();
        overviewSitesComponent.getStylesheets().clear();
        overviewUpgradeComponent.getStylesheets().clear();
        sitesComponent.getStylesheets().clear();

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
          "developed"
        );
    }

    @Test
    public void createIslandData(){
        assertEquals(0,islandsService.getListOfIslands().size());
        islandsService.retrieveIslands("game1");
        gameSystemsApiService.getSystems("game1");

        sleep(100);

        List<Island> testIsles = islandsService.getListOfIslands();

        testIsleComps = islandsService.createIslands(testIsles);
        Map<String,IslandComponent> testIsleMap = islandsService.getComponentMap();



        sleep(100);

        assertEquals(3,testIsles.size());
        assertEquals(3,testIsleComps.size());
        assertEquals(3,testIsleMap.size());
        List<Line> lines = islandsService.createLines(testIsleMap);
        assertEquals(2,lines.size());
        assertNotNull(islandsService.getEmpire("empire"));
        assertNotEquals(0,islandsService.getMapWidth());
        assertNotEquals(0,islandsService.getMapHeight());
        assertEquals(2,islandsService.getSiteManagerSize());




        Platform.runLater(() -> {
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
        sleep(1000);
        Platform.runLater(() -> {
            waitForFxEvents();
            clickOn("#showStorageButton");
            waitForFxEvents();

            sleep(500);

        });
        assertTrue(inGameController.storageOverviewComponent.isVisible());
        sleep(500);
        Platform.runLater(() -> {
            waitForFxEvents();
            clickOn("#showStorageButton");
            waitForFxEvents();

            sleep(500);

        });




        sleep(2000);

        assertEquals(37,islandsService.getAllNumberOfSites("empire"));
        assertEquals(17,islandsService.getCapacityOfOneSystem("home"));
        assertEquals(9,islandsService.getNumberOfSites("empire","energy"));



        sleep(2000);

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
        buttons[2].setOnAction(event -> inGameController.showStorage());
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
        assertFalse(this.inGameController.storageOverviewContainer.isVisible());
        // Open storage
        clickOn("#showStorageButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewContainer.isVisible());
        // Close storage with Button in Ingame
        clickOn("#showStorageButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewContainer.isVisible());
        // Open again
        clickOn("#showStorageButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewContainer.isVisible());
        // Close storage with button in StorageOverviewComponent
        clickOn("#closeStorageOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewContainer.isVisible());
    }

}