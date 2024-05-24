package de.uniks.stp24;

import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.utils.ResponseConstants;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Locale;
import java.util.ResourceBundle;


import static org.mockito.Mockito.spy;

/**
 * Base class for controller tests
 */
public class ControllerTest extends ApplicationTest {

    @Spy
    public App app = spy(App.class);
    @Spy
    public PrefService prefService;
    @Spy
    public ResponseConstants responseConstants;
    @Spy
    public ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stp24/lang/main", Locale.ROOT);

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
        stage.getScene().getStylesheets().clear();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        app.stop();
        app = null;
        stage = null;
    }

    @AfterAll
    public static void tearDown() {
        Mockito.framework().clearInlineMocks();
    }
    @AfterEach
    public void tearDown2() {
        System.gc();
    }

    /*public void clear() {
        imageCache.clear();
    }*/
}
