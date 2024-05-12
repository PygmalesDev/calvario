package de.uniks.stp24;

import de.uniks.stp24.component.PauseMenuComponent;
import de.uniks.stp24.component.SettingsComponent;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.InGameService;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class PauseMenuTest extends ControllerTest {
    @Spy
    Game game;

    @Spy
    InGameService inGameService;

    @Mock
    PauseMenuComponent pauseMenuComponent;

    @Mock
    SettingsComponent settingsComponent;

    @InjectMocks
    InGameController inGameController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        inGameService.setGame(game);
        doReturn(game).when(this.inGameService).getGame();
        this.app.show(this.inGameController);
    }

    @Test
    public void testPausing() {
        press(KeyCode.ESCAPE);
        waitForFxEvents();
        assertTrue(game.getPaused());
    }

    @Test
    public void testChangeLanguage() {
        press(KeyCode.ESCAPE);
        waitForFxEvents();
        // pauseMenuComponent.setInGameService(inGameService);
        clickOn("#settingsButton");
        waitForFxEvents();
        // settingsComponent.setInGameService(inGameService);
        clickOn("#germanLang");
        waitForFxEvents();
        assertEquals(0, inGameService.getLanguage());
        clickOn("#englishLang");
        waitForFxEvents();
        assertEquals(1, inGameService.getLanguage());
    }
}