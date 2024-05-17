package de.uniks.stp24;

import javafx.stage.Stage;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Base class for controller tests
 */
public class ControllerTest extends ApplicationTest {

    @Spy
    public final App app = new App();

    protected Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.stage = stage;
        stage.setX(0);
        stage.setY(0);
        stage.requestFocus();
        app.start(stage);
    }
}
