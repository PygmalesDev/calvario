package de.uniks.stp24.game;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.controllers.EditGameController;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditGameService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditGameControllerTest extends ControllerTest {
    @InjectMocks
    EditGameController editGameController;

    @Mock
    BrowseGameService browseGameService;

    @Mock
    BrowseGameController browseGameController;

    @Spy
    Subscriber subscriber = spy(Subscriber.class);

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
//        verify(editGameService).editGame(any(), any(), any());

    }

    @Test
    public void testCancel() {
        doReturn(null).when(app).show("/browseGames");
        clickOn("#editGameCancelButton");
        WaitForAsyncUtils.waitForFxEvents();

        verify(app, times(1)).show("/browseGames");

    }
}
