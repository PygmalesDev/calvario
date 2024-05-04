package de.uniks.stp24;

import de.uniks.stp24.controllers.SignUpController;
import de.uniks.stp24.dto.SignUpResultDto;
import de.uniks.stp24.service.SignUpService;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestSignUpNewUser extends ControllerTest {
    @Spy
    SignUpService signUpService;
    @InjectMocks
    SignUpController signUpController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.app.show(this.signUpController);
    }

    @Test
    public void testSignup() {
        doReturn(Observable.just(new SignUpResultDto("a", "b", "c", "d", "e" )))
                .when(this.signUpService).register(any(),any());
        doReturn(null).when(app).show("/login");

        assertEquals("SignUp", stage.getTitle());
        assertTrue(this.signUpController.registerButton.disableProperty().get());

        clickOn("#usernameField");
        write("TemplateUser");
        assertTrue(this.signUpController.registerButton.disableProperty().get());

        clickOn("#passwordField");
        write("TemplateUserPassword");
        assertTrue(this.signUpController.registerButton.disableProperty().get());

        clickOn("#repeatPasswordField");
        write("TemplateUserPassword");
        assertFalse(this.signUpController.registerButton.disableProperty().get());

        clickOn("#registerButton");

        waitForFxEvents();

        verify(this.signUpService, times(1))
                .register("TemplateUser", "TemplateUserPassword");
        verify(this.app, times(1))
                .show("/login");
    }
}
