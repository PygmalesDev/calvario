package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.model.Site;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "IslandClaiming.fxml")
public class IslandClaimingComponent extends Pane {
    @FXML
    Text islandTypeText;
    @FXML
    Text colonizersText;
    @FXML
    Text timeText;
    @FXML
    Text capacityText;
    @FXML
    ImageView capacityImage;
    @FXML
    ImageView colonizersImage;
    @FXML
    ImageView timerImage;
    @FXML
    Button exploreButton;
    @FXML
    Button cancelJobButton;
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    Pane colonizePane;
    @FXML
    Text noSitesText;
    @FXML
    ListView<Site> sitesListView;
    @FXML
    ListView<de.uniks.stp24.model.Resource> consumeListView;
    @FXML
    ListView<de.uniks.stp24.model.Resource> costsListView;

    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public JobsService jobsService;
    @Inject
    public ImageCache imageCache;
    @Inject
    public IslandsService islandsService;
    @Inject
    public Subscriber subscriber;
    @Inject
    App app;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    public Provider<ClaimingSiteComponent> componentProvider;
    Provider<ResourceComponent> negativeResourceProvider = () -> new ResourceComponent("negative", gameResourceBundle, this.imageCache);
    private ObservableList<Job> upgradeJobs;
    private final ObservableList<Site> siteObservableList = FXCollections.observableArrayList();
    private final ObservableList<de.uniks.stp24.model.Resource> consumeObservableList = FXCollections.observableArrayList();
    private final ObservableList<de.uniks.stp24.model.Resource> costsObservableList = FXCollections.observableArrayList();
    private Island currentIsland;
    private Job islandJob;
    private double incrementAmount;
    private double progress;

    @Inject
    public IslandClaimingComponent() {}

    @OnRender
    public void render() {
        this.jobsService.onJobsLoadingFinished(() ->
                this.upgradeJobs = this.jobsService.getJobObservableListOfType("upgrade"));

        this.timerImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/time.png"));
        this.capacityImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/capacity_icon.png"));
        this.colonizersImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/crewmates_icon.png"));
        this.setPickOnBounds(false);
    }

    public void setIslandInformation(Island island) {
        this.currentIsland = island;
        this.islandTypeText.setText(this.gameResourceBundle.getString(islandTranslation.get(island.type().toString())));
        this.capacityText.setText(String.valueOf(island.resourceCapacity()));
        this.colonizersText.setText(String.valueOf(island.crewCapacity()));

        this.islandJob = this.upgradeJobs.stream()
                .filter(job -> job.system().equals(this.currentIsland.id())).findFirst().orElse(null);
        if (Objects.nonNull(this.islandJob)) {
            this.setProgressBarVisibility(true);
            this.progress = this.islandJob.progress();
            this.incrementAmount = (double) 1 / this.islandJob.total();
            this.jobProgressBar.setProgress(this.progress * this.incrementAmount);
        } else this.setProgressBarVisibility(false);

        if (this.currentIsland.upgrade().equals("explored")) {
            this.siteObservableList.clear();
            this.consumeObservableList.clear();
            this.costsObservableList.clear();
            this.timeText.setText("12");

            island.sitesSlots().forEach((name, amount) -> this.siteObservableList
                    .add(new Site(name, null, null, null, 0, amount)));
            this.sitesListView.setItems(this.siteObservableList);
            this.sitesListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.componentProvider));
            this.noSitesText.setVisible(this.sitesListView.getItems().isEmpty());

            this.islandAttributes.systemUpgradeAttributes.colonized().cost().forEach((name, amount) -> this.costsObservableList
                    .add(new de.uniks.stp24.model.Resource(name, amount, 0)));
            this.costsListView.setItems(this.costsObservableList);
            this.costsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.negativeResourceProvider));

            this.islandAttributes.systemUpgradeAttributes.colonized().upkeep().forEach((name, amount) -> this.consumeObservableList
                    .add(new de.uniks.stp24.model.Resource(name, amount, 0)));
            this.consumeListView.setItems(this.consumeObservableList);
            this.consumeListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.negativeResourceProvider));

            this.exploreButton.setText(this.gameResourceBundle.getString("claiming.colonize"));
            this.colonizePane.setVisible(true);
        } else {
            this.exploreButton.setText(this.gameResourceBundle.getString("claiming.explore"));
            this.colonizePane.setVisible(false);
            this.timeText.setText("3");
        }
    }

    public void exploreIsland() {
        this.subscriber.subscribe(this.jobsService.beginJob(Jobs.createIslandUpgradeJob(this.currentIsland.id())), job -> {
            this.setProgressBarVisibility(true);
            this.progress = 0;
            this.islandJob = job;
            this.jobProgressBar.setProgress(this.progress);
            this.incrementAmount = (double) 1 /job.total();
            this.jobsService.onJobCompletion(job._id(), () -> {
                if (this.currentIsland.id().equals(job.system())) this.setProgressBarVisibility(false);
            });
        }, error -> System.out.printf(
                        """
                        Creating a new exploration job failed in IsalndClaimingComponent
                        An exception was caught here: %s
                        """, error.getMessage()));

    }

    @OnRender
    public void setJobUpdaters() {
        this.jobsService.onJobsLoadingFinished("upgrade", (job) ->
            this.jobsService.onJobCompletion(job._id(), () -> {
                if (Objects.nonNull(this.currentIsland))
                    if (this.currentIsland.id().equals(job.system())) this.getParent().setVisible(false);
            }));
        this.jobsService.onJobTypeProgress("upgrade", (someJob) -> {
            if (Objects.nonNull(this.currentIsland))
                if (someJob.system().equals(this.currentIsland.id())) this.incrementProgress();
        });
    }

    private void incrementProgress() {
        this.progress++;
        this.jobProgressBar.setProgress(this.progress*this.incrementAmount);
    }

    private void setProgressBarVisibility(boolean isVisible) {
        this.exploreButton.setVisible(!isVisible);
        this.cancelJobButton.setVisible(isVisible);
        this.jobProgressBar.setVisible(isVisible);
    }

    public void close() {
        this.getParent().setVisible(false);
    }

    public void cancelJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.islandJob._id()));
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
