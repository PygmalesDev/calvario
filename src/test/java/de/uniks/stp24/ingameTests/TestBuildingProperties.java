package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class TestBuildingProperties extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    PresetsApiService presetsApiService;
    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;
    @Spy
    TimerService timerService;

    @Spy
    EventService eventService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    LanguageService languageService;
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    EmpireService empireService;
    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);

    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    ClockComponent clockComponent;
    @InjectMocks
    OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;
    @Spy
    IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    DetailsComponent detailsComponent;
    @InjectMocks
    SitesComponent sitesComponent;
    @InjectMocks
    BuildingsComponent buildingsComponent;
    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;
    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;
    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;
    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;

    @InjectMocks
    EventComponent eventComponent;

    @InjectMocks
    InGameController inGameController;

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    ArrayList<String> buildings = new ArrayList<>();
/*

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.clockComponent.timerService = this.timerService;
        this.clockComponent.subscriber = this.subscriber;
        this.islandsService.app = this.app;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.overviewUpgradeComponent= this.overviewUpgradeComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameService.setGameStatus(gameStatus);
        this.inGameService.presetsApiService = this.presetsApiService;
        Map<String , Integer> chance = new HashMap<>();
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();

        Map<String, Integer> siteSlots = new HashMap<>();
        Map<String, Integer> sites = new HashMap<>();

        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");

        Map<String, Integer> resources1 = Map.of("energy",3);
        Map<String, Integer> resources2 = Map.of("energy",3, "population", 4);
        Map<String, Integer> resources3 = Map.of("energy",5, "population", 6);
        buildings.add("mine");
        // Mock TokenStorage
        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(island).when(this.islandAttributeStorage).getIsland();
        doReturn(island).when(this.tokenStorage).getIsland();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, resources1 ,
                null))).when(this.empireService).getEmpire(any(),any());

        doReturn(Observable.just(new Game("a","a","testGameID", "gameName", "gameOwner", true,1,1,null ))).when(gamesApiService).getGame(any());

        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));


        //Todo: Maybe not the smartest solution
        doReturn(Observable.just(new SystemUpgrades(null, null, null, null, null))).when(presetsApiService).getSystemUpgrades();
        doReturn(Observable.just(new DistrictPresets(null, null, null, null, null))).when(presetsApiService).getDistrictPresets();
        doReturn(Observable.just(new BuildingPresets(null, null, null, null))).when(presetsApiService).getBuildingPresets();

        this.app.show(this.inGameController);


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
    public void deleteBuilding(){
        waitForFxEvents();
        press(KeyCode.I);
        sitePropertiesComponent.setVisible(false);
        waitForFxEvents();
        Map<String, Integer> siteSlots = new HashMap<>();
        Map<String, Integer> sites = new HashMap<>();
        Map<String, Integer> links = new HashMap<>();
        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");
        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).destroyBuilding(any(), any(),any());
        doReturn(new Island(island.owner(),1, island.posX(), island.posY(), island.type(), island.crewCapacity(),
                island.resourceCapacity(), island.upgradeLevel(), island.sitesSlots(),
                island.sites(), island.buildings(), island.id(), "explored")).when(islandsService).updateIsland(any());
        doNothing().when(islandsService).updateIslandBuildings(any(), any(), any());
        waitForFxEvents();
        clickOn("#buildingMine");
        waitForFxEvents();
        clickOn("#destroyButton");
        waitForFxEvents();
        clickOn("#confirmButton");
        waitForFxEvents();

        verify(this.resourcesService, times(1)).destroyBuilding(any(), any(),any());
        verify(this.islandsService, times(2)).updateIsland(any());

    }

    @Test
    public void buyBuilding(){
        waitForFxEvents();
        press(KeyCode.I);
        sitePropertiesComponent.setVisible(false);
        waitForFxEvents();
        Map<String, Integer> siteSlots = new HashMap<>();
        Map<String, Integer> sites = new HashMap<>();
        Map<String, Integer> links = new HashMap<>();
        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");
        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        doReturn(new Island(island.owner(),1, island.posX(), island.posY(), island.type(), island.crewCapacity(),
                island.resourceCapacity(), island.upgradeLevel(), island.sitesSlots(),
                island.sites(), island.buildings(), island.id(), "explored")).when(islandsService).updateIsland(any());
        waitForFxEvents();
        clickOn("#buildingFarm");
        waitForFxEvents();
        clickOn("#buyButton");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());
        verify(this.islandsService, times(2)).updateIsland(any());

    }
*/
}
