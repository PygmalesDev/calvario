package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
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
import java.util.Objects;
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

    @Inject
    ImageCache imageCache;

    @Inject
    IslandsService islandsService;
    @Inject
    JobsService jobsService;
    @Inject
    Subscriber subscriber;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    private Job job;

    @Inject
    public JobElementComponent() {

    }

    @Override
    public void setItem(@NotNull Job job) {
        this.job = job;

        this.timerText.setText(String.format("%s/%s", job.progress(), job.total()));
        if (Objects.nonNull(job.system()))
            this.jobNameText.setText(this.islandsService.getIslandName(job.system()));
        else this.jobNameText.setText("Empire Technologies");

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
            // TODO: Change upgrade to next upgrade level
            case "upgrade" -> {
                this.jobImage.setImage(this.imageCache.get("de/uniks/stp24/icons/other/upgrade_job.png"));
                this.jobTypeText.setText(String.format("Upgrading island to %s", job.type()));
            }
        }
    }

    public void cancelJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job));
    }

    public void showJobOverview() throws Exception {
        this.jobsService.getJobInspector("island_jobs_overview").accept(new String[]{this.job.system()});
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
