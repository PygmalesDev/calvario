package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
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
import java.util.ResourceBundle;

@Component(view = "JobElement.fxml")
public class JobElementComponent extends Pane implements ReusableItemComponent<Job> {
    @FXML
    public ImageView jobImage;
    @FXML
    public Text jobNameText;
    @FXML
    public Text jobTypeText;
    @FXML
    public Text timerText;
    @FXML
    Button jobCancelButton;
    @FXML
    Button inspectionButton;

    @Inject
    public ImageCache imageCache;

    @Inject
    public IslandsService islandsService;
    @Inject
    public JobsService jobsService;
    @Inject
    public Subscriber subscriber;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;
    private Job job;

    @Inject
    public JobElementComponent() {

    }

    @Override
    public void setItem(@NotNull Job job) {
        this.job = job;
        this.jobCancelButton.setId("jobElementDeleteButton_" + job._id());
        this.inspectionButton.setId("jobElementInspectionButton_" + job._id());

        Island island = this.islandsService.getIsland(job.system());
        this.timerText.setText(String.format("%s/%s", job.progress(), (int) job.total()));
        this.jobNameText.setText(island.name());

        switch (job.type()) {
            case "building" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.buildingsIconPathsMap.get(job.building())));
                this.jobTypeText.setText(this.gameResourceBundle.getString(
                        Constants.buildingTranslation.get(job.building())));
            }
            case "district" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.sitesIconPathsMap.get(job.district())));
                this.jobTypeText.setText(this.gameResourceBundle.getString(
                        Constants.siteTranslation.get(job.district())) + " Site");
            }
            case "upgrade" -> {
                this.jobImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/other/upgrade_job.png"));
                this.jobTypeText.setText(this.gameResourceBundle.getString("jobs."+island.upgrade()));
            }
        }
    }

    public void cancelJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job));
    }

    public void showJobOverview() {
        this.jobsService.getJobInspector("island_jobs_overview").accept(job);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
