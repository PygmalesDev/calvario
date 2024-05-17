package de.uniks.stp24.end;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.controllers.LoginController;
import de.uniks.stp24.controllers.LogoutController;
import de.uniks.stp24.model.LogoutResult;
import de.uniks.stp24.service.LogoutService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LogoutControllerTest extends ControllerTest {
    @Spy
    LogoutService logoutService;
    @Spy
    Subscriber subscriber;
    @Spy
    LoginController loginController;
    @InjectMocks
    LogoutController logoutController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(logoutController);
    }

    @Test
    public void clickOnLogout() {
        doReturn(Observable.just(new LogoutResult(""))).when(logoutService).logout(any());

        // Start:
        // Alice sees the Logout
        assertEquals("Logout", stage.getTitle());

        // Alice clicks on logout
        clickOn("#logoutButton");
        waitForFxEvents();

        // Alice sees now the login screen
        verify(logoutService,times(1)).logout("");
        assertEquals("Login", stage.getTitle());
    }
    @Test
    public void clickOnCancel() {
        doReturn(null).when(app).show("/browseGames");

        // Start:
        // Alice has unintended clicked the logout button
        // and see the logout screen
        assertEquals("Logout", stage.getTitle());

        // Alice clicks on cancel
        clickOn("#cancelButton");
        waitForFxEvents();

        // Alice return to the browse game login screen
        // must be fixed when browsegames.fxml is available
        assertEquals("Logout", stage.getTitle());
    }

}
