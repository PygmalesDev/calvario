package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.UpgradeStatus;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
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

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class PauseMenuTest extends ControllerTest {


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

    @InjectMocks
    ClockComponent clockComponent;

    @InjectMocks
    PauseMenuComponent pauseMenuComponent;

    @InjectMocks
    SettingsComponent settingsComponent;

    @InjectMocks
    OverviewSitesComponent overviewSitesComponent;

    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;

    @InjectMocks
    IslandAttributeStorage islandAttributeStorage;

    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;

    @InjectMocks
    DetailsComponent detailsComponent;

    @InjectMocks
    SitesComponent sitesComponent;

    @InjectMocks
    BuildingsComponent buildingsComponent;

    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);


    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;

        inGameService.setGameStatus(gameStatus);
        inGameService.setTimerService(timerService);
        doReturn(Observable.just(new Game("a", "a", "gameId", "gameName", "gameOwner", true, 1, 1, null))).when(gamesApiService).getGame(any());
        SystemUpgrades systemUpgrades = new SystemUpgrades(
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0));
        doReturn(Observable.just(systemUpgrades)).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(new EmpireDto(
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                "1",
                1,
                -1,
                "1",
                null,
                null,
                null
        ))).when(empireService).getEmpire(any(), any());

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

        press(KeyCode.ESCAPE);
        waitForFxEvents();
        press(KeyCode.Q);
//        clickOn("#quitButton");
        waitForFxEvents();

        verify(app, times(1)).show("/browseGames");
    }

}