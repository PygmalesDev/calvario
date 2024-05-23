package de.uniks.stp24;

import de.uniks.stp24.component.BubbleComponent;
import de.uniks.stp24.controllers.LoginController;
import de.uniks.stp24.service.PrefService;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class LicensesControllerTest extends ControllerTest {

    @InjectMocks
    LoginController loginController;

    @Spy
    BubbleComponent bubbleComponent;

    @Spy
    Subscriber subscriber = new Subscriber();


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(loginController);
    }

    @Test
    void clickOnLicenses(){
        //Start:
        //Alice is in the login screen. She wants to see licenses and credits.
        assertEquals(resources.getString("login"), stage.getTitle());

        //Action:
        //She clicks licenses button.
        clickOn("#licensesButton");

        waitForFxEvents();

        //Result:
        //Alice is in the license screen
        assertEquals("Licenses", stage.getTitle());
    }

}