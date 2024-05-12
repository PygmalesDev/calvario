package de.uniks.stp24;

import de.uniks.stp24.controllers.CreateGameController;

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

    @Test
    void noInput(){
        sleep(5000);
        clickOn(createGameController.createGameConfirmButton);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("CreateGame", stage.getTitle());
    }
}
