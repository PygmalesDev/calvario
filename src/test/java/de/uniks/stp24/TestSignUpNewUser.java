package de.uniks.stp24;

import de.uniks.stp24.controllers.SignUpController;
import de.uniks.stp24.service.SignUpService;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        app.show(signUpController);
    }

    @Test
    public void signup() {
        doReturn(true).when(signUpService).register(any(),any());
        // TODO: Understand why this line isn't needed??
        // doReturn(null).when(app).show("/login");

        // TODO: Somebody has to change the name of the stage or idk
        // assertEquals("Signup", stage.getTitle());

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

        verify(this.signUpService, times(1)).register("TemplateUser", "TemplateUserPassword");
        verify(this.app, times(1)).show("/login");
    }
}
