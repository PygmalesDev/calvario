package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Objects;

@Component(view = "JobsOverview.fxml")
public class JobsOverviewComponent extends AnchorPane {
    @FXML
    ListView<Job> jobsListView;
    @FXML
    Button closeButton;
    @Inject
    JobsService jobsService;
    private ObservableList<Job> jobsList;

    @Inject
    App app;
    @Inject
    Provider<JobElementComponent> jobProvider;

    @Inject
    public JobsOverviewComponent() {
    }

    @OnRender
    public void setJobsObservableList() {
        this.jobsService.onJobsLoadingFinished(() -> {
            this.jobsList = this.jobsService.getObservableJobCollection();
            this.jobsListView.setItems(this.jobsList);
            this.jobsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.jobProvider));
        });
    }
}
