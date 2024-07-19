package de.uniks.stp24.ingameTests;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.HelpComponent;
import de.uniks.stp24.model.TechHelp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class HelpComponentTest extends ControllerTest {
    
    @InjectMocks
    HelpComponent helpComponent;

    @Spy
    ResourceBundle technologiesResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/technologies", Locale.ROOT);
    ObservableList<TechHelp> technologies = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        technologies.add(new TechHelp("test1", "test1"));
        helpComponent.technologies = technologies;
        this.app.show(this.helpComponent);
        helpComponent.getStylesheets().clear();
    }

    @Test
    public void checkListview(){
        waitForFxEvents();
        press(KeyCode.ALT, KeyCode.H);
        assertEquals(0, helpComponent.technologyTagsListView.getItems().size());
    }

    @Test
    public void close(){
        waitForFxEvents();
        clickOn("#closeButton");
        assertEquals(false, helpComponent.visibleProperty().getValue());
    }
}
