package de.uniks.stp24;

import de.uniks.stp24.controllers.LoginController;
import de.uniks.stp24.dto.LoginResult;
import de.uniks.stp24.service.LoginService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import javafx.stage.Stage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LoginControllertest extends ControllerTest {
    @Spy
    LoginService loginService;
    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(loginController);
    }

    @Test
    void login(){
        doReturn(Observable.just(new LoginResult("1", "a","b","c","d"))).when(loginService).login(any(),any());
        doReturn(null).when(app).show("/browseGames");

        // Start:
        // Alice has started the game STPellar. She sees the Log in screen.
        // She has already registered an account and wants to log in.
        assertEquals("Login", stage.getTitle());

        // Action:
        // Alice enters her username “alice999” and password “1234”. She clicks Login
        clickOn("#usernameInput");
        write("alice999");
        clickOn("#passwordInput");
        write("1234");
        clickOn("#loginButton");

        waitForFxEvents();

        // Result:
        // She logged in successfully. She can now navigate through game states
        verify(loginService, times(1)).login("alice999", "1234");
        verify(app, times(1)).show("/browseGames");
    }

    @Test
    void changeToRegisterScreen(){
    }

}
