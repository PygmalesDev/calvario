package de.uniks.stp24;

import de.uniks.stp24.controllers.EditAccController;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class TestEditAcc extends ControllerTest{

    @InjectMocks
    EditAccController editAccController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(editAccController);
    }


    @Test
    void switchToBrowseGamesScreen(){
        doReturn(null).when(app).show("/browseGames");
        // Title: Going back from user adminstration screen to browse game screen
        // Start: Alice is currently in the user administration screen. She wants to go back to the browse game screen

        // Action: She clicks on go back.
        clickOn("#goBackButton");

        // Result: The window changed to the edit user Game screen window.
        waitForFxEvents();
        verify(app, times(1)).show("/browseGames");
    }



}
