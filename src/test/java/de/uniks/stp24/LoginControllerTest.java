package de.uniks.stp24;

import de.uniks.stp24.component.BubbleComponent;
import de.uniks.stp24.controllers.LoginController;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.service.LoginService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.CheckBox;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.stage.Stage;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LoginControllerTest extends ControllerTest {
    @Spy
    LoginService loginService;
    @Spy
    Subscriber subscriber;
    @Spy
    BubbleComponent bubbleComponent;
    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(loginController);
    }

    @Test
    void login(){
        doReturn(Observable.just(new LoginResult("1", "a","b","c","d")))
                .when(loginService).login(any(),any(), eq(false));
        doReturn(null).when(app).show("/browseGames");

        // Start:
        // Alice has started the game STPellar. She sees the Log in screen.
        // She has already registered an account and wants to log in.
        assertEquals("LOGIN", stage.getTitle());

        // Action:
        // Alice writes her username and her password
        clickOn("#usernameInput");
        write("alice999");
        clickOn("#passwordInput");
        write("1234");
        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        // Result:
        // She logged in successfully. She can now navigate through game states
        verify(loginService, times(1)).login("alice999", "1234", false);
        WaitForAsyncUtils.waitForFxEvents();
        verify(app, times(1)).show("/browseGames");
    }
    @Test
    void noLoginIfMissingInputs() {

        // Start:
        // Alice is a curious person, who wants to play STPellar.
        // She is in the log in screen and wonders what would happen,
        // if she enters login without entering a name or password.
        assertEquals("LOGIN", stage.getTitle());

        // Action:

        // Alice enters her username "alice999" and password "1234". She clicks Login
        doubleClickOn("#usernameInput");
        write("");
        doubleClickOn("#passwordInput");
        write("");
        clickOn("#loginButton");

        waitForFxEvents();

        // Result:
        // Alice remains in the login screen.
        // The window states that she has put in name and password for login.
        String text = lookup("#errorLabel").queryText().getText();

        assertFalse((text.isBlank() || text.isEmpty()));

    }

    @Test
    void switchToRegisterScreen(){
        // Title: Switch to register screen
        // Start:
        // Alice wants to start playing STPellar. However, she has no account to play with.

        // Action:
        // Alice clicks register.
        clickOn("#signupButton");

        // Result:
        // Her screen switches to the register window.
        waitForFxEvents();
        assertEquals("REGISTER", stage.getTitle());
    }

    @Test
    void clickOnRememberMe(){
        
        // Start:
        // Alice is in the log in screen.
        // She has put in her username and password. Next time she logs in she wants to use the same account
        assertEquals("LOGIN", stage.getTitle());
        CheckBox rememberMe = (CheckBox) lookup("#rememberMeBox").queryParent();
        assertFalse(rememberMe.isSelected());

        clickOn("#usernameInput");
        write("alice999\t");
        write("1234");

        // Alice activates the remember me checkbox
        // Action:

        clickOn("#rememberMeBox");
//
        // Result:
        // The game will remember now she her account data.

        assertTrue(rememberMe.isSelected());
    }

}
