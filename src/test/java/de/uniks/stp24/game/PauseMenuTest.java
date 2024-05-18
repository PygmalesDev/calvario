package de.uniks.stp24.game;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.PauseMenuComponent;
import de.uniks.stp24.component.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.LanguageService;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.inject.Inject;
import javax.inject.Provider;
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
    LanguageService languageService;

    @Spy
    PauseMenuComponent pauseMenuComponent;

    @Spy
    SettingsComponent settingsComponent;

    @Spy
    Provider<ResourceBundle> newResources;

    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        inGameService.setGame(gameStatus);
        doReturn(gameStatus).when(this.inGameService).getGame();
        this.app.show(this.inGameController);
    }

    @Test
    public void testPausing() {
        press(KeyCode.ESCAPE);
        waitForFxEvents();
        assertTrue(gameStatus.getPaused());
    }

    @Test
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
    }

    @Test
    public void testQuitting() {
        doNothing().when(pauseMenuComponent).quit();

        press(KeyCode.ESCAPE);
        waitForFxEvents();
        clickOn("#quitButton");
        waitForFxEvents();

        verify(this.pauseMenuComponent).quit();
    }

}