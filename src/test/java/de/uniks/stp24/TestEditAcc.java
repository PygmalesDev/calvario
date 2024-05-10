package de.uniks.stp24;

import de.uniks.stp24.component.WarningScreenComponent;
import de.uniks.stp24.controllers.EditAccController;
import de.uniks.stp24.dto.SignUpResultDto;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;


@ExtendWith(MockitoExtension.class)
public class TestEditAcc extends ControllerTest{
    @Spy
    EditAccService editAccService;
    @InjectMocks
    EditAccController editAccController;
    @Spy
    WarningScreenComponent warningScreenComponent;
    @Spy
    TokenStorage tokenStorage;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(editAccController);
    }


    @Test
    void switchToBrowseGamesScreen(){
        doReturn(null).when(app).show("/browseGames");
        // Title: Going back from user administration screen to browse game screen
        // Start: Alice is currently in the user administration screen. She wants to go back to the browse game screen

        // Action: She clicks on go back.
        clickOn("#goBackButton");

        // Result: The window changed to the edit user Game screen window.
        waitForFxEvents();
        verify(app, times(1)).show("/browseGames");
    }

    @Test
    void cancelChangeAccount(){
        // Title: Cancel changing editing account in the user administration window
        //doReturn(Observable.just(new SignUpResultDto("a", "b", "c", "d", "e" )))
                //.when(this.editAccService).changeUserInfo(any(),any());
        // Start:
        // Alice wants to change her Account name in STPellaris. She is in the user administrtation screen and has clicked on edit account.
        tokenStorage.setName("Alice");
        Button changeUserInfoButton = lookup("#changeUserInfoButton").queryButton();
        Button cancelChangesButton = lookup("#cancelChangesButton").queryButton();
        assertEquals(cancelChangesButton.isVisible(), false);
        clickOn("#changeUserInfoButton");
        assertEquals(changeUserInfoButton.isVisible(), true);
        assertEquals(changeUserInfoButton.isDisabled(), true);

        // Action:
        // She changes name and or password but changes her mind and clicks canel
        clickOn("#usernameInput");
        write("Calvario");
        clickOn("#passwordInput");
        write("password");
        clickOn("#cancelChangesButton");
        waitForFxEvents();
        // Result:
        // Alice’s Account has not changed and she is still in the user administration screen.
        TextField username = lookup("#usernameInput").query();
        assertEquals(username.isDisabled(), true);
        assertEquals(username.getText(), "Alice");
    }

    @Test
    void changeAccount() {
    }





}
