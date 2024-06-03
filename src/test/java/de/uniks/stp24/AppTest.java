package de.uniks.stp24;

import de.uniks.stp24.service.TokenStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppTest extends ControllerTest {

    @Spy
    public final App app = new App();
    @Inject
    TokenStorage tokenStorage;

    @Test
    public void v1() {
        // TODO login, main-menu, ...
    }

}
