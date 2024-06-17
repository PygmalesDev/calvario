package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestBuildingsWindow extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;

    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;
    @Spy
    TimerService timerService;
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
    SettingsComponent settingsComponent;
    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    ClockComponent clockComponent;

    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;

    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;

    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;

    @InjectMocks
    InGameController inGameController;

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> siteSlots = new HashMap<>();
    Map<String, Integer> sites = new HashMap<>();
    Map<String, Integer> links = new HashMap<>();
    String[] buildings = new String[]{"mine", "exchange", "farm"};


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameService.setGameStatus(gameStatus);
        Map<String , Integer> chance = new HashMap<>();
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();

        Map<String, Integer> resources1 = Map.of("energy",3);
        Map<String, Integer> resources2 = Map.of("energy",3, "population", 4);
        Map<String, Integer> resources3 = Map.of("energy",5, "population", 6);

        // Mock TokenStorage
        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        doReturn(Observable.just(new SiteDto("a",chance, required,production, consumption))).when(resourcesService).getResourcesSite(any());
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, resources1 ,
                null))).when(this.empireService).getEmpire(any(),any());

        doReturn(Observable.just(new Game("a","a","testGameID", "gameName", "gameOwner", true,1,1,null ))).when(gamesApiService).getGame(any());

        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));


        this.app.show(this.inGameController);

        buildingPropertiesComponent.setVisible(false);
        sitePropertiesComponent.setVisible(false);
    }

    @Test
    public void buyExchange(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingExchange");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyPowerPlant(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingPowerPlant");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyMine(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingMine");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyFarm(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingFarm");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyResearchLab(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingResearchLab");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyFoundry(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingFoundry");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyFactory(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingFactory");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }

    @Test
    public void buyRefinery(){
        waitForFxEvents();

        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        clickOn("#buildingRefinery");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());

    }





}
