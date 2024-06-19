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
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
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
import javafx.scene.input.KeyCode;
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
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestBuildingProperties extends ControllerTest {
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
        String[] buildings = new String[]{"mine", "exchange", "farm"};
        Island island = new Island("testOwner", Upgrade.explored, "", "testID", 1, 500.0, 500.0,
                IslandType.mining, 20, 20, 1, siteSlots, sites, buildings);
        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).destroyBuilding(any(), any());
        doReturn(new Island(island.owner(), island.upgrade(), island.name(), island.id_(), island.flagIndex(),
                island.posX(), island.posY(), island.type(), island.crewCapacity(), island.resourceCapacity(), island.upgradeLevel(), island.sitesSlots(),
                island.sites(), island.buildings())).when(islandsService).updateIsland(any());
        waitForFxEvents();
        clickOn("#buildingFarm");
        waitForFxEvents();
        clickOn("#destroyButton");

        verify(this.resourcesService, times(1)).destroyBuilding(any(), any());
        verify(this.islandsService, times(1)).updateIsland(any());

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
        String[] buildings = new String[]{"mine", "exchange", "farm"};
        Island island = new Island("testOwner", Upgrade.explored, "", "testID", 1, 500.0, 500.0,
                IslandType.mining, 20, 20, 1, siteSlots, sites, buildings);
        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).createBuilding(any(), any(),any());
        doReturn(new Island(island.owner(), island.upgrade(), island.name(), island.id_(), island.flagIndex(),
                island.posX(), island.posY(), island.type(), island.crewCapacity(), island.resourceCapacity(), island.upgradeLevel(), island.sitesSlots(),
                island.sites(), island.buildings())).when(islandsService).updateIsland(any());
        waitForFxEvents();
        clickOn("#buildingFarm");
        waitForFxEvents();
        clickOn("#buyButton");

        verify(this.resourcesService, times(1)).createBuilding(any(), any(),any());
        verify(this.islandsService, times(1)).updateIsland(any());

    }
}
