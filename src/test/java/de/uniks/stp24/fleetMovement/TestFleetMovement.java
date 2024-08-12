package de.uniks.stp24.fleetMovement;

import de.uniks.stp24.appTestModules.AppTest3Module;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

@ExtendWith(MockitoExtension.class)
public class TestFleetMovement extends AppTest3Module {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);

        this.joinGameHelper.joinGame(GAME_ID, true);

    }

    @Test
    public void test() {
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#islandID_2_instance");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#travelButton");

        // Please don't remove this sleep calls; I need them to test animations
        GAME_SUBJECT.onNext(tickGame());
        sleep(50);
        GAME_SUBJECT.onNext(tickGame());
        sleep(50);
        GAME_SUBJECT.onNext(tickGame());
        sleep(50);
        GAME_SUBJECT.onNext(tickGame());

        sleep(10000);
    }
}
