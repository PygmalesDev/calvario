package de.uniks.stp24;

import de.uniks.stp24.appTestModules.AppTest3Module;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.ws.Event;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AppTest3 extends AppTest3Module {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);
    }

    @Test
    public void testApp() {
        this.selectTraits();
        this.loadGame();
        this.beginIslandClaiming();
    }

    public void selectTraits() {
        clickOn("#selectEmpireButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#editButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#chooseTraitsButton");
        WaitForAsyncUtils.waitForFxEvents();

        ListView<Trait> traitListView = lookup("#selectedTraitsListView").queryListView();

        clickOn("#preparedButtonChoose");
        assertEquals(1, traitListView.getItems().size());
        clickOn("#unpreparedButtonChoose");
        assertEquals(1, traitListView.getItems().size());

        clickOn("#preparedButtonUnChoose");
        assertEquals(0, traitListView.getItems().size());
        clickOn("#__dev__ButtonChoose");
        assertEquals(1, traitListView.getItems().size());

        clickOn("#traitsConfirmButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1, lookup("#confirmedTraitsListView").queryListView().getItems().size());

        clickOn("#confirmButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#selectButton");
    }

    public void loadGame() {
        WaitForAsyncUtils.waitForFxEvents();
        MEMBER_DTO_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".members.*.updated", MEMBER_DTO2));
        clickOn("#startJourneyButton");
    }

    public void beginIslandClaiming() {
        clickOn("#jobsOverviewButton");

        clickOn("#islandID_2_instance");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#exploreButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATH + "created", JOBS[0]));
//        clickOn("#islandID_2_instance");
//
//        clickOn("#islandID_3_instance");
//        WaitForAsyncUtils.waitForFxEvents();
//        clickOn("#exploreButton");
//        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATH + "created", JOBS[1]));

        GAME_SUBJECT.onNext(this.tickGame());
        WaitForAsyncUtils.waitForFxEvents();
        GAME_SUBJECT.onNext(this.tickGame());


        sleep(100000);
    }
}
