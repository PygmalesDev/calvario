package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.component.game.technology.ResearchJobComponent;
import de.uniks.stp24.component.game.technology.TechnologyCategoryComponent;
import de.uniks.stp24.component.game.technology.TechnologyOverviewComponent;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.dto.EffectSourceParentDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.*;
import de.uniks.stp24.service.game.*;
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
    JobsService jobsService;

    @Spy
    VariableService variableService;
    @Spy
    GamesApiService gamesApiService;

    @Spy
    GameSystemsApiService gameSystemsApiService;

    @Spy
    EmpireApiService empireApiService;

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
    ImageCache imageCache;

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
    OverviewSitesComponent overviewSitesComponent;

    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;


    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;

    @InjectMocks
    IslandAttributeStorage islandAttributeStorage;

    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;

    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;

    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;

    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;

    @InjectMocks
    IslandOverviewJobsComponent islandOverviewJobsComponent;
    @InjectMocks
    IslandClaimingComponent islandClaimingComponent;


    @InjectMocks
    DetailsComponent detailsComponent;

    @InjectMocks
    SitesComponent sitesComponent;

    @InjectMocks
    BuildingsComponent buildingsComponent;

    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;
    @InjectMocks
    EmpireOverviewComponent empireOverviewComponent;

    @InjectMocks
    HelpComponent helpComponent;

    @InjectMocks
    PropertiesJobProgressComponent propertiesJobProgressComponent;

    @InjectMocks
    VariableExplanationComponent variableExplanationComponent;

    @InjectMocks
    TechnologyOverviewComponent technologyOverviewComponent;

    @InjectMocks
    TechnologyCategoryComponent technologyCategoryComponent;
    @InjectMocks
    ResearchJobComponent researchJobComponent;

    @Spy
    JobsApiService jobsApiService;
    @Spy
    TechnologyService technologyService;

    @Spy
    ResourceBundle technologiesResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/technologies", Locale.ROOT);

    @InjectMocks
    InGameController inGameController;

    ArrayList<BuildingPresets> buildingPresets = new ArrayList<>();
    ArrayList<DistrictPresets> districtPresets = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.eventComponent.empireApiService = this.empireApiService;
        inGameService.setEventService(eventService);
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.helpComponent = this.helpComponent;
        this.inGameController.variableExplanationComponent = this.variableExplanationComponent;
        this.inGameController.technologiesComponent = technologyOverviewComponent;
        this.technologyOverviewComponent.technologyCategoryComponent = technologyCategoryComponent;
        this.technologyCategoryComponent.researchJobComponent = researchJobComponent;


        this.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.timerService.gamesApiService = this.gamesApiService;
        this.timerService.subscriber = this.subscriber;
        this.timerService.tokenStorage = this.tokenStorage;

        this.jobsService.subscriber = this.subscriber;
        this.jobsService.jobsApiService = this.jobsApiService;
        this.jobsService.tokenStorage = this.tokenStorage;
        this.jobsService.eventListener = this.eventListener;
        this.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;
        this.sitePropertiesComponent.siteJobProgress = this.propertiesJobProgressComponent;

        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;

        this.inGameService.presetsApiService = this.presetsApiService;

        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.islandClaimingComponent.jobsService = this.jobsService;
        this.islandClaimingComponent.islandAttributes = this.islandAttributeStorage;
        this.islandClaimingComponent.islandsService = this.islandsService;
        this.islandClaimingComponent.imageCache = this.imageCache;

        doReturn(null).when(this.imageCache).get(any());

        doReturn(Observable.empty()).when(this.jobsApiService).getEmpireJobs(any(), any());

        inGameService.setGameStatus(gameStatus);
        inGameService.setTimerService(timerService);
        Map<String , Integer> chance = new HashMap<>();
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        UpgradeStatus upgradeStatus = new UpgradeStatus("test", 20, production, consumption, 20);

        doReturn(Observable.just(new EmpireDto("a","b","c", "a","a","a","a","a",1, 2, "a", new String[]{"1"}, Map.of("energy",3) , null))).when(this.empireService).getEmpire(any(),any());
        doReturn(Observable.just(new Game("a","a","gameId", "gameName", "gameOwner", 2, 0,true,1,1,null ))).when(gamesApiService).getGame(any());
        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        doReturn(Observable.just(new SystemUpgrades(upgradeStatus,upgradeStatus, upgradeStatus, upgradeStatus, upgradeStatus ))).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(new ArrayList<BuildingPresets>())).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(new ArrayList<DistrictPresets>())).when(inGameService).loadDistrictPresets();

        doReturn(Observable.just(new EffectSourceParentDto(new EffectSourceDto[]{}))).when(empireApiService).getEmpireEffect(any(), any());

        doNothing().when(variableService).initVariables();

        this.app.show(this.inGameController);

        this.storageOverviewComponent.getStylesheets().clear();
        this.pauseMenuComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
        this.eventComponent.getStylesheets().clear();
        this.storageOverviewComponent.getStylesheets().clear();
        this.overviewSitesComponent.getStylesheets().clear();
        this.overviewUpgradeComponent.getStylesheets().clear();
        this.buildingsComponent.getStylesheets().clear();
        this.sitesComponent.getStylesheets().clear();
        this.detailsComponent.getStylesheets().clear();
        this.deleteStructureComponent.getStylesheets().clear();
        sitePropertiesComponent.getStylesheets().clear();
        buildingsWindowComponent.getStylesheets().clear();
        buildingPropertiesComponent.getStylesheets().clear();
        this.jobsOverviewComponent.getStylesheets().clear();
        islandClaimingComponent.getStylesheets().clear();
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