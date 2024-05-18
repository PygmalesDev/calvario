package de.uniks.stp24;

import de.uniks.stp24.service.PrefService;
import javafx.stage.Stage;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.prefs.Preferences;

/**
 * Base class for controller tests
 */
public class ControllerTest extends ApplicationTest {

    @Spy
    public final App app = new App();
    @Spy
    PrefService prefService;

    protected Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.stage = stage;
        stage.requestFocus();
        this.prefService.removeRefreshToken();
        app.start(stage);
    }
}
