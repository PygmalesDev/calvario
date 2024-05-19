package de.uniks.stp24;

import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.component.WarningComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.controllers.EditGameController;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditGameService;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class EditGameControllerTest extends ControllerTest {
    @InjectMocks
    EditGameController editGameController;

    @Mock
    GamesApiService gamesApiService;

    @Mock
    BrowseGameService browseGameService;

    @Mock
    GameComponent gameComponent;
    @Mock
    BrowseGameController browseGameController;

    @Spy
    WarningComponent warningComponent;
    @Spy
    Subscriber subscriber = new Subscriber();

    @Mock
    EditGameService editGameService;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(editGameController);
    }

    @Test
    void editGameWithInputs(){
        editGameController.setEditGameService(editGameService);

        WaitForAsyncUtils.waitForFxEvents();


        clickOn("#editNameTextField");
        write("testgame95");

        clickOn("#editPasswordTextField");
        write("1");
        clickOn("#editRepeatPasswordTextField");
        write("1");

        WaitForAsyncUtils.waitForFxEvents();

        Button confirmButton = lookup("#editGameConfirmButton").queryButton();
        clickOn(confirmButton);
        WaitForAsyncUtils.waitForFxEvents();

        //Confirm if the game function that edits the game was invoked
        verify(editGameService, times(1)).editGame(any(), any(), any());
    }

    @Test
    public void testCancel() {
        doReturn(null).when(app).show("/browseGames");
        clickOn("#editGameCancelButton");
        WaitForAsyncUtils.waitForFxEvents();

        verify(app, times(1)).show("/browseGames");

    }
}
