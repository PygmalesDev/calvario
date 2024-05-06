package de.uniks.stp24;

import de.uniks.stp24.service.ImageCache;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ImageCacheTest extends ApplicationTest {

    @Inject
    ImageCache imageCache;

    @Spy
    public final App app = new App();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        app.setComponent(DaggerTestComponent.builder().mainApp(app).build());
        app.start(stage);
        stage.requestFocus();
    }

    @Test
    public void basicTest() {
        assertNull(imageCache);
        imageCache = new ImageCache();
        imageCache.get("icon");
        imageCache.get("data:hallo");

    }

}
