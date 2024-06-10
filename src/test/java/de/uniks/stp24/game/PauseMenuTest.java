package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.EventListener;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class PauseMenuTest extends ControllerTest {


    @Spy
    GameStatus gameStatus;

    @Spy
    InGameService inGameService;

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
    PauseMenuComponent pauseMenuComponent;

    @InjectMocks
    SettingsComponent settingsComponent;

    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;

    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);


    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        inGameService.setGameStatus(gameStatus);
        doReturn(gameStatus).when(this.inGameService).getGameStatus();
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