package de.uniks.stp24.game;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.controllers.CreateGameController;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CreateGameControllerTest extends ControllerTest {
    @InjectMocks
    CreateGameController createGameController;

    @Override
    public void start(Stage stage)  throws Exception{
        super.start(stage);
        app.show(createGameController);
    }

    //Check if screen changes although there is no input for creating a new game.
    @Test
    void noInput(){
        Button confirmButton = lookup("#createGameConfirmButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Create Game", stage.getTitle());
    }
}
