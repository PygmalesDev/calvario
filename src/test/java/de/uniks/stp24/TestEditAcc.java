package de.uniks.stp24;

import de.uniks.stp24.component.WarningScreenComponent;
import de.uniks.stp24.controllers.EditAccController;
import de.uniks.stp24.dto.SignUpResultDto;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.model.User;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;
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
    @Spy
    Subscriber subscriber;

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
        assertFalse(cancelChangesButton.isVisible());
        clickOn("#changeUserInfoButton");
        assertTrue(changeUserInfoButton.isVisible());
        assertTrue(changeUserInfoButton.isDisabled());

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
        assertTrue(username.isDisabled());
        assertEquals(username.getText(), "Alice");
    }

    @Test
    void changeAccount() {
        doReturn(Observable.just(new User("Calvario", "a","b","c","d"))).when(editAccService).changeUserInfo(any(),any());

        // Title: Confirming edited account
        // Start:
        // Alice wants to change her Account name in STPellaris. She is in the user administrtation screen and has clicked on edit account.
        tokenStorage.setName("Alice");
        clickOn("#changeUserInfoButton");

        //        Action:
        // She puts in a new name and password. She clicks the confirm button
        clickOn("#usernameInput");
        write("Calvario");
        clickOn("#passwordInput");
        write("password");
        clickOn("#saveChangesButton");
        waitForFxEvents();
        // Result:
        // Alice stays in the same screen but her name and password changed
        TextField username = lookup("#usernameInput").query();
        assertTrue(username.isDisabled());
        assertEquals("Calvario",username.getText());
        TextField password = lookup("#passwordInput").query();
        assertTrue(password.isDisabled());
        assertEquals("",password.getText());
    }

    @Test
    void cancelDeleteAccountTest(){
        //Title: Cancel after clicking delete account button
        //Start:
        //Alice wants to delete her account in STPellaris. She is in the user administration window. She clicked the delete user button and a pop up came up.
        tokenStorage.setName("Alice");
        HBox editAccHBox = (HBox) lookup("#editAccHBox").query();
        StackPane warningScreenContainer = (StackPane) lookup("#warningScreenContainer").query();
        clickOn("#deleteUserButton");
        waitForFxEvents();

        //Action:
        //She clicks cancel because she changed her mind.
        Button cancelDeleteButton = lookup("#cancelDeleteButton").queryButton();
        Button deleteUserButton = lookup("#deleteUserButton").queryButton();
        assertTrue(cancelDeleteButton.isVisible());
        assertFalse(cancelDeleteButton.isDisabled());
        assertEquals(BoxBlur.class, editAccHBox.getEffect().getClass());

        //Result:
        //The pop up disappeared. Alice stays in the same window and her account was not deleted.
        clickOn("#cancelDeleteButton");
        waitForFxEvents();
        assertNull(editAccHBox.getEffect());
        assertFalse(warningScreenContainer.isVisible());
        assertTrue(deleteUserButton.isVisible());
        assertFalse(deleteUserButton.isDisabled());
        assertEquals("Alice", tokenStorage.getName());
    }

    @Test
    void deleteAccountTest(){
        // Todo!!!
    }





}
