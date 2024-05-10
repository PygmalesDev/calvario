package de.uniks.stp24;

import de.uniks.stp24.service.TokenStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AppToolsTest extends ApplicationTest {

    @Spy
    public final App app = new App();
    @Inject
    TokenStorage tokenStorage;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        app.setComponent(DaggerTestComponent.builder().mainApp(app).build());
        app.start(stage);
        stage.requestFocus();
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
