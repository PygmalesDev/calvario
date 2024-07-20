package de.uniks.stp24;

import de.uniks.stp24.appTestModules.AppTest3Module;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.ws.Event;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

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

        clickOn("#jobsOverviewButton");
        this.beginSiteJob();
        this.beginBuildingJob();
        this.testInspectJobs();
        this.testJobCancellation();
        this.beginIslandClaiming();
        sleep(100000);
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

    public void beginSiteJob() {
        clickOn("#islandID_1_instance");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#sitesButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#energy");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildSiteButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[3] + "created", JOBS[3]));
    }

    public void beginBuildingJob() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildingsButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#empty_building_element");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildingRefinery");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buyButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[2] + "created", JOBS[2]));
        clickOn("#closeButton");
    }

    public void testInspectJobs() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobElementInspectionButton_jobSiteID");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobProgressInspectionButton_jobSiteID");
    }

    public void testJobCancellation() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobElementDeleteButton_jobSiteID");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[3] + "deleted", JOBS[3]));
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeButton");
    }

    public void beginIslandClaiming() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#islandID_2_instance");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#exploreButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[0] + "created", JOBS[0]));
        clickOn("#islandID_2_instance");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#islandID_3_instance");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#exploreButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[1] + "created", JOBS[1]));

        GAME_SUBJECT.onNext(this.tickGame());
        WaitForAsyncUtils.waitForFxEvents();
        GAME_SUBJECT.onNext(this.tickGame());

    }
}
