package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.game.JobsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobProgress.fxml")
public class IslandOverviewJobProgressComponent extends Pane implements ReusableItemComponent<Job> {
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    Text jobTimeRemaining;
    @FXML
    Button stopJobButton;
    @FXML
    Button infoJobButton;
    @FXML
    Text jobDescriptionText;
    @FXML
    ImageView jobImage;
    @FXML
    HBox costsHBox;

    @Inject
    JobsService jobsService;

    @Inject
    Subscriber subscriber;

    private Job job;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    public IslandOverviewJobProgressComponent() {

    }
    @Override
    public void setItem(@NotNull Job job) {
        this.job = job;
        this.jobProgressBar.setProgress((((double) 1/job.total()) * job.progress()));
        switch (job.type()) {
            // TODO: Add translations
            case "building" -> this.jobDescriptionText.setText(String.format("Building of %s", job.building()));
            case "district" -> this.jobDescriptionText.setText(String.format("Building on %s", job.district()));
            // TODO: Change upgrade to next upgrade level
            case "upgrade" -> this.jobDescriptionText.setText(String.format("Upgrading island to %s", job.type()));
        }
        this.jobTimeRemaining.setText(String.format("%s/%s", job.progress(), job.total()));
    }

    public void stopJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job));
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
