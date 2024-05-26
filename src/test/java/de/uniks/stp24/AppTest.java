package de.uniks.stp24;

import de.uniks.stp24.service.TokenStorage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppTest extends ApplicationTest {

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

    public void v1() {
        // TODO login, main-menu, ...
    }

}
