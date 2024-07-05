package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

import de.uniks.stp24.service.Constants;

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
    Text jobPositionText;
    @FXML
    ImageView jobImage;
    @FXML
    HBox costsHBox;
    @FXML
    ListView<de.uniks.stp24.model.Resource> costsListView;
    Provider<ResourceComponent> resourceComponentProvider = () ->
            new ResourceComponent(true, false,
                    true, false, this.gameResourceBundle);
    private final ObservableList<de.uniks.stp24.model.Resource> resourceObservableList = FXCollections.observableArrayList();

    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    JobsService jobsService;
    @Inject
    ImageCache imageCache;
    @Inject
    App app;

    @Inject
    Subscriber subscriber;

    private Job job;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    public IslandOverviewJobProgressComponent() {
        this.setPickOnBounds(true);
    }

    @Override
    public void setItem(@NotNull Job job) {
        this.job = job;
        this.jobProgressBar.setProgress((((double) 1/job.total()) * job.progress()));
        ObservableList<Job> systemJobs = this.jobsService.getObservableListForSystem(
                this.islandAttributes.getIsland().id());
        if (systemJobs.indexOf(job) != 0) {
            this.jobProgressBar.setVisible(false);
            this.jobTimeRemaining.setVisible(false);
        }

        this.jobPositionText.setText(systemJobs.indexOf(job)+1 + ".");
        this.jobTimeRemaining.setText(String.format("%s/%s", job.progress(), job.total()));

        this.resourceObservableList.clear();
        job.cost().forEach((name, count) -> this.resourceObservableList
                .add(new de.uniks.stp24.model.Resource(name, count, 0)));
        this.costsListView.setItems(this.resourceObservableList);
        this.costsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.resourceComponentProvider));
        this.costsListView.setMouseTransparent(true);

        switch (job.type()) {
            case "building" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.buildingsIconPathsMap.get(job.building())));
                this.jobDescriptionText.setText(this.gameResourceBundle.getString(
                        Constants.buildingTranslation.get(job.building())));
            }
            case "district" -> {
                this.jobImage.setImage(this.imageCache.get("/"+ Constants.sitesIconPathsMap.get(job.district())));
                this.jobDescriptionText.setText(this.gameResourceBundle.getString(
                        Constants.siteTranslation.get(job.district())) + " Site");
            }
            // TODO: Change upgrade to next upgrade level
            case "upgrade" -> {
                this.jobImage.setImage(this.imageCache.get("de/uniks/stp24/icons/other/upgrade_job.png"));
                this.jobDescriptionText.setText(String.format("Upgrading island to %s", job.type()));
            }
        }
    }

    public void showJobDetails() throws Exception {
        switch (this.job.type()) {
            case "district" -> this.jobsService.getJobInspector("site_overview")
                    .accept(new String[]{this.job.district(), this.job.system()});
            case "building" -> this.jobsService.getJobInspector("building_overview")
                    .accept(new String[]{this.job.building(), this.job._id(), this.job.system()});
        }

    }

    public void stopJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job));
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
