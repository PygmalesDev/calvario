package de.uniks.stp24;

import de.uniks.stp24.controllers.LoadController;
import de.uniks.stp24.controllers.LoginController;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitFor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@ExtendWith(MockitoExtension.class)
public class LoadTest extends ControllerTest{
    @InjectMocks
    LoadController loadController;
    @InjectMocks
    LoginController loginController;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        app.show(loadController);
    }

    @Test
    void testLoad() throws TimeoutException {
        assertEquals(stage.getTitle(), "Game Name");
        waitFor(10, TimeUnit.SECONDS, () -> {
            return !stage.getTitle().equals("Game Name");
        });
        assertEquals(stage.getTitle(), "Login");
    }

}
