package de.uniks.stp24.battles;

import de.uniks.stp24.ws.Event;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(MockitoExtension.class)
public class TestBattles extends BattlesModule {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);
        this.joinGameHelper.joinGame(GAME_ID, true);
    }

    @Test
    public void test() {
//        WaitForAsyncUtils.waitForFxEvents();
//        clickOn("#ingameFleet_testFleetID_1");
//
//        WaitForAsyncUtils.waitForFxEvents();
//        clickOn("#enemyIslandID_instance");
//
//        clickOn("#travelButton");
//
//        sleep(10000);
    }
}
