package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class PauseMenuTest extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;

    @Spy
    GameSystemsApiService gameSystemsApiService;

    @Spy
    PresetsApiService presetsApiService;

    @Spy
    TokenStorage tokenStorage;

    @Spy
    PopupBuilder popupBuilder;
    @Spy
    EventService eventService;

    @Spy
    GameStatus gameStatus;

    @Spy
    InGameService inGameService;

    @Spy
    LobbyService lobbyService;

    @Spy
    TimerService timerService;

    @Spy
    Subscriber subscriber = spy(Subscriber.class);

    @Spy
    LanguageService languageService;

    @Spy
    ResourcesService resourcesService;

    @Spy
    ObjectMapper objectMapper;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    EmpireService empireService;


    @InjectMocks
    ClockComponent clockComponent;

    @InjectMocks
    EventComponent eventComponent;

    @InjectMocks
    PauseMenuComponent pauseMenuComponent;

    @InjectMocks
    SettingsComponent settingsComponent;

    @InjectMocks
    EmpireOverviewComponent empireOverviewComponent;

    @InjectMocks
    OverviewSitesComponent overviewSitesComponent;

    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;

    @InjectMocks
    IslandAttributeStorage islandAttributeStorage;

    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;

    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;

    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;

    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;

    @InjectMocks
    IslandOverviewJobsComponent islandOverviewJobsComponent;


    @InjectMocks
    DetailsComponent detailsComponent;

    @InjectMocks
    SitesComponent sitesComponent;

    @InjectMocks
    BuildingsComponent buildingsComponent;

    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;



    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);


    @InjectMocks
    InGameController inGameController;

    ArrayList<BuildingPresets> buildingPresets = new ArrayList<>();
    ArrayList<DistrictPresets> districtPresets = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = this.eventComponent;
        inGameService.setEventService(eventService);
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.timerService.gamesApiService = this.gamesApiService;
        this.timerService.subscriber = this.subscriber;
        this.timerService.tokenStorage = this.tokenStorage;

        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;

        this.inGameService.presetsApiService = this.presetsApiService;


        inGameService.setGameStatus(gameStatus);
        inGameService.setTimerService(timerService);
        Map<String , Integer> chance = new HashMap<>();
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        UpgradeStatus upgradeStatus = new UpgradeStatus("test", 20, production, consumption, 20);

        doReturn(Observable.just(new EmpireDto("a","b","c", "a","a","a","a","a",1, 2, "a", new String[]{"1"}, Map.of("energy",3) , null))).when(this.empireService).getEmpire(any(),any());
        doReturn(Observable.just(new Game("a","a","gameId", "gameName", "gameOwner", 2,true,1,1,null ))).when(gamesApiService).getGame(any());
        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        doReturn(Observable.just(new SystemUpgrades(upgradeStatus,upgradeStatus, upgradeStatus, upgradeStatus, upgradeStatus ))).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(new ArrayList<BuildingPresets>())).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(new ArrayList<DistrictPresets>())).when(inGameService).loadDistrictPresets();

        this.app.show(this.inGameController);
    }

    @Test
    public void testPausing() {
        press(KeyCode.ESCAPE);
        waitForFxEvents();
        assertTrue(gameStatus.getPaused());
    }

    /*@Test
    public void testChangeLanguage() {
        settingsComponent.prefService = this.prefService;
        languageService.prefService = this.prefService;
        languageService.newResources = this.newResources;

        doAnswer(show -> {inGameService.setShowSettings(true);
            return null;
        }).when(pauseMenuComponent).settings();

        doAnswer(show -> {inGameService.setLanguage(0);
            return null;
        }).when(settingsComponent).setToGerman();

        doAnswer(show -> {inGameService.setLanguage(1);
            return null;
        }).when(settingsComponent).setToEnglish();

        press(KeyCode.ESCAPE);
        waitForFxEvents();

        clickOn("#settingsButton");
        waitForFxEvents();

        clickOn("#germanLang");
        waitForFxEvents();
        assertEquals(0, inGameService.getLanguage());

        clickOn("#englishLang");
        waitForFxEvents();
        assertEquals(1, inGameService.getLanguage());
    }*/

    @Test
    public void testQuitting() {
        doReturn(null).when(app).show("/browseGames");

        tokenStorage.setEmpireId("empireId");
        tokenStorage.setGameId("gameId");

        press(KeyCode.ESCAPE);
        waitForFxEvents();
        press(KeyCode.Q);
//        clickOn("#quitButton");
        waitForFxEvents();

        assertNull(tokenStorage.getEmpireId());
        assertNull(tokenStorage.getGameId());
        verify(app, times(1)).show("/browseGames");
    }

}