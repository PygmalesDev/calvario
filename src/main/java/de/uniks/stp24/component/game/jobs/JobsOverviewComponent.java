package de.uniks.stp24.component.game.jobs;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "JobsOverview.fxml")
public class JobsOverviewComponent extends AnchorPane {
    @FXML
    ListView<JobElementComponent> jobsListView;
    @FXML
    Button closeButton;

    @Inject
    public JobsOverviewComponent() {

    }
}
