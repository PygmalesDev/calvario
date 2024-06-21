package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.EventComponent;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

@ExtendWith(MockitoExtension.class)
public class IslandsServiceTest extends ControllerTest {
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
    LanguageService languageService;

    @Spy
    ResourcesService resourcesService;
    @Spy
    GameSystemsApiService gameSystemsApiService;

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
/*
    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = eventComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.clockComponent.timerService = this.timerService;
        this.clockComponent.eventService = this.eventService;
        this.clockComponent.subscriber = this.subscriber;
        this.clockComponent.gamesApiService = this.gameApiService;
        this.clockComponent.islandsService = this.islandsService;
        this.clockComponent.eventComponent = this.eventComponent;
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

        inGameController.mapScrollPane = new ScrollPane();
        inGameController.group = new Group();
        inGameController.zoomPane = new StackPane();
        inGameController.mapGrid = new Pane();

        inGameController.zoomPane.getChildren().add(inGameController.mapGrid);
        inGameController.group.getChildren().add(inGameController.zoomPane);
        inGameController.mapScrollPane.setContent(inGameController.group);

        doReturn(gameStatus).when(this.inGameService).getGameStatus();
        doReturn(Observable
                .just(new Game("a", null, "game1Id", "testGame1",
                        "testHost1", true, 1,10, null))).when(gameApiService).getGame(any());
        doReturn(null).when(this.app).show("/ingame");
        islandsService.saveEmpire("empire",new ReadEmpireDto("a","b","empire","game1","user1","name",
                "description","#FFDDEE",2,3,"home"));
        SystemDto[] systems = new SystemDto[3];
        ArrayList<String> buildings = new ArrayList<>(Arrays.asList("power_plant", "mine", "farm", "research_lab", "foundry", "factory", "refinery"));
        systems[0] = new SystemDto("a","b","system1","game1","agriculture",
                "name",null,null,25,null, Upgrade.unexplored,0,
                Map.of("home",22),1.46,-20.88,null);
        systems[1] = new SystemDto("a","b","system2","game1","energy",
                "name",null,null,16,null, Upgrade.unexplored,0,
                Map.of("home",18),-7.83,-11.04,null);
        systems[2] = new SystemDto("a","b","home","game1","uninhabitable_0", "name",
                Map.of("city",3, "industry", 3, "mining",3, "energy",3, "agriculture",3),
                Map.of("city",3, "industry", 3, "mining",3, "energy",3, "agriculture",3), 22,
                buildings,Upgrade.developed,25,Map.of("system1",22,"system2",18),-5.23,4.23,"empire"
        );

        IslandComponent comp0 = new IslandComponent();
        comp0.setLayoutX(systems[0].x());
        comp0.setLayoutY(systems[0].y());
        IslandComponent comp1 = new IslandComponent();
        comp1.setLayoutX(systems[1].x());
        comp1.setLayoutY(systems[1].y());
        IslandComponent comp2 = new IslandComponent();
        comp2.setLayoutX(systems[2].x());
        comp2.setLayoutY(systems[2].y());
        Map<String, IslandComponent> compMap = Map.of("system1", comp0,
                "system2", comp1,
                "home" , comp2);
        List<IslandComponent> compList = Arrays.asList(comp0,comp1,comp2);
        doReturn(Observable.just(systems)).when(gameSystemsApiService).getSystems(any());
        doReturn(compMap).when(islandsService).getComponentMap();
        doReturn(compList).when(islandsService).createIslands(any());

        doReturn(Observable.just(buildingPresets)).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(districtPresets)).when(inGameService).loadDistrictPresets();
        doReturn(Observable.just(systemUpgrades)).when(inGameService).loadUpgradePresets();

        Mockito.doCallRealMethod().when(islandsService).retrieveIslands(any());
        Mockito.doCallRealMethod().when(islandsService).getListOfIslands();
        Mockito.doCallRealMethod().when(islandsService).getMapWidth();
        Mockito.doCallRealMethod().when(islandsService).getMapHeight();
        Mockito.doCallRealMethod().when(islandsService).getEmpire(any());
        Mockito.doCallRealMethod().when(islandsService)
                .createLines(any());

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, new LinkedHashMap<>() {{put("energy", 5);put("population", 4);}},
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
        sitePropertiesComponent.getStylesheets().clear();
        buildingsWindowComponent.getStylesheets().clear();
        buildingPropertiesComponent.getStylesheets().clear();
        deleteStructureComponent.getStylesheets().clear();
    }

    @Test
    public void createIslandData(){

        assertEquals(0,islandsService.getListOfIslands().size());
        islandsService.retrieveIslands("game1");
        gameSystemsApiService.getSystems("game1");

        sleep(1000);

        List<Island> testIsles = islandsService.getListOfIslands();
        List<IslandComponent> testIsleComps = islandsService.createIslands(testIsles);
        Map<String,IslandComponent> testIsleMap = islandsService.getComponentMap();

        sleep(1000);

        assertEquals(3,islandsService.getListOfIslands().size());
        assertEquals(3,islandsService.createIslands(testIsles).size());
        assertEquals(3,islandsService.getComponentMap().size());
        assertEquals(2,islandsService.createLines(testIsleMap).size());
        assertNotNull(islandsService.getEmpire("empire"));
        assertNotEquals(0,islandsService.getMapWidth());
        assertNotEquals(0,islandsService.getMapHeight());

    }

 */
}