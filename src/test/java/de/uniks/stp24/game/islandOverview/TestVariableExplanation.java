package de.uniks.stp24.game.islandOverview;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestVariableExplanation extends IslandOverviewTestComponent{

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.getScene().getStylesheets().clear();
        initComponents();
        inGameController.showOverview();
    }

    @Test
    public void testTooltip(){

    }

    @Test
    public void testVariablesTree(){

    }

    @Test
    public void testVariablesDependency(){

    }
}
