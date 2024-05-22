package de.uniks.stp24;

import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.utils.ResponseConstants;
import javafx.stage.Stage;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.prefs.Preferences;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Base class for controller tests
 */
public class ControllerTest extends ApplicationTest {

    @Spy
    public App app = new App();
    @Spy
    protected PrefService prefService;
    @Spy
    ResponseConstants responseConstants;
    @Spy
    ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stp24/lang/main", Locale.ROOT);

    protected Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.stage = stage;
        stage.setX(0);
        stage.setY(0);
        stage.requestFocus();
        this.prefService.removeRefreshToken();
        prefService.setLocale(Locale.ENGLISH);
        app.start(stage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        app.stop();
        app = null;
        stage = null;
    }
}
