package de.uniks.stp24.battles;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestBattles extends BattlesModule {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
    }

    @Test
    public void test() {}
}
