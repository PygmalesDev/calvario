package de.uniks.stp24.tools;

import de.uniks.stp24.App;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.DaggerTestComponent;
import de.uniks.stp24.service.TokenStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppToolsTest extends ControllerTest {
    @Inject
    TokenStorage tokenStorage;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        app.start(stage);
    }

    @Test
    public void basicTokenStorage() {
        tokenStorage = new TokenStorage();
        assertNull(tokenStorage.getToken());
        assertNull(tokenStorage.getUserId());
        tokenStorage.setToken("hallo");
        tokenStorage.setUserId("user");
        assertFalse(tokenStorage.getToken().isEmpty());
        assertFalse(tokenStorage.getUserId().isEmpty());

    }

}
