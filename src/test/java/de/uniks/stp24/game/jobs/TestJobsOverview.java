package de.uniks.stp24.game.jobs;

import de.uniks.stp24.component.game.jobs.JobElementComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.model.Jobs.*;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestJobsOverview extends JobsTestComponent {
    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;

    Provider<JobElementComponent> jobElementComponentProvider = () -> {
        JobElementComponent comp = new JobElementComponent();
        comp.islandsService = islandsService;
        comp.imageCache = imageCache;
        comp.jobsService = jobsService;
        comp.subscriber = subscriber;
        comp.gameResourceBundle = gameResourceBundle;
        return comp;
    };

    ListView<Job> jobListView;
    ObservableList<Job> jobs;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        doReturn(null).when(this.imageCache).get(any());

        this.jobsOverviewComponent.jobProvider = this.jobElementComponentProvider;
        this.jobsOverviewComponent.jobsService = this.jobsService;

        this.app.show(this.jobsOverviewComponent);
        this.jobsOverviewComponent.getStylesheets().clear();
    }

    @BeforeEach
    public void getListView() {
        WaitForAsyncUtils.waitForFxEvents();
        this.jobListView = lookup("#jobsListView").queryListView();
        this.jobs = jobListView.getItems();
    }

    @Test
    public void testInitializingOverview() {
        // Assert that only the running island jobs are shown
        assertEquals(4, this.jobs.size());
    }

    @Test
    public void testDeletingJob() {
        when(this.jobsApiService.deleteJob(eq(this.GAME_ID), eq(this.EMPIRE_ID), any()))
                .thenReturn(Observable.just(this.jobsList.get(0)));

        String jobID = this.jobs.get(0)._id();

        this.callSubjectEvent(EVENT.DELETED, jobID);
        clickOn("#jobElementDeleteButton_" + jobID);
        sleep(10000);
    }

    @Test
    public void testClosingOverview() {
        clickOn("#closeButton");
        assertFalse(this.jobsOverviewComponent.isVisible());
    }

    @Test
    public void testOpeningInspectionWindow() {
        String jobID = this.jobs.get(0)._id();
        clickOn("#jobElementInspectionButton_" + jobID);

        assertEquals(1, this.inspectorCalls.get("overview"));
    }
}
