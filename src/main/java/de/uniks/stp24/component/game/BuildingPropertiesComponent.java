package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.BuildingDto;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ExplanationService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.*;

import static de.uniks.stp24.service.Constants.buildingTranslation;
import static de.uniks.stp24.service.Constants.buildingsIconPathsMap;

@Component(view = "BuildingProperties.fxml")
public class BuildingPropertiesComponent extends AnchorPane {

    @FXML
    ListView<Resource> buildingCostsListView;
    @FXML
    Button buyButton;
    @FXML
    ListView<Resource> buildingProducesListView;
    @FXML
    ListView<Resource> buildingConsumesListView;
    @FXML
    Button closeButton;
    @FXML
    Button destroyButton;
    @FXML
    Text buildingName;
    @FXML
    ImageView buildingImage;
    @FXML
    Pane jobProgressPane;
    @Inject
    public JobsService jobsService;
    @Inject
    public ResourcesService resourcesService;
    @Inject
    Subscriber subscriber;
    @Inject
    IslandsService islandsService;
    @Inject
    @SubComponent
    public PropertiesJobProgressComponent propertiesJobProgressComponent;
    @Inject
    App app;
    @Inject
    public GameSystemsApiService gameSystemsApiService;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    @Inject
    ExplanationService explanationService;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    IslandAttributeStorage islandAttributeStorage;
    Map<String, String> buildingsMap;
    String buildingType;
    InGameController inGameController;
    Map<String, Integer> priceOfBuilding;
    ObservableList<Jobs.Job> buildingJobs;

    String currentJobID = "";

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle);


    @OnInit
    public void init(){
        buildingsMap = buildingsIconPathsMap;
    }

    @Inject
    public BuildingPropertiesComponent(){

    }

    @OnRender
    public void render() {
        this.setPickOnBounds(false);
        this.jobProgressPane.setPickOnBounds(false);
        this.propertiesJobProgressComponent.setPickOnBounds(false);
        this.jobProgressPane.getChildren().add(this.propertiesJobProgressComponent);
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }


    public void setBuildingType(String buildingType, String jobID){
        this.buildingType = buildingType;
        this.currentJobID = jobID;
        displayInfoBuilding();
        disableButtons();
        startResourceMonitoring();

        if (!jobID.isEmpty()) {
            Jobs.Job job = this.buildingJobs.stream()
                    .filter(started -> started._id().equals(jobID)
                            && started.system().equals(this.tokenStorage.getIsland().id()))
                    .findFirst().orElse(null);

            this.showJobsPane();
            if (Objects.nonNull(job)) {
                this.propertiesJobProgressComponent.setJobProgress(job);
                if (this.jobsService.hasNoJobTypeProgress(job.type()) && this.buildingJobs.getFirst().equals(job))
                    this.jobsService.onJobTypeProgress(job.type(), () ->
                            this.propertiesJobProgressComponent.incrementProgress());
                else this.jobsService.stopOnJobTypeProgress("building");
            }
        } else {
            this.hideJobsPane();
            this.jobsService.stopOnJobTypeProgress("building");
        }

    }

    @OnInit
    public void setBuildingUpdates() {
        this.jobsService.onJobsLoadingFinished("building", job -> {
            this.jobsService.onJobDeletion(job._id(), () -> {
                if (this.currentJobID.equals(job._id()))
                    this.getParent().setVisible(false);
                this.updateIslandBuildings();

                if (this.jobsService.hasNoJobTypeProgress(job.type()) &&
                        (this.buildingJobs.isEmpty() || this.buildingJobs.getFirst()._id().equals(currentJobID)))
                    this.jobsService.onJobTypeProgress(job.type(), () ->
                            this.propertiesJobProgressComponent.incrementProgress());
            });

            this.jobsService.onJobCommonFinish(this::updateIslandBuildings);

            this.jobsService.onJobCompletion(job._id(), () -> {
                this.updateIslandBuildings();
                this.hideJobsPane();

                if (this.jobsService.hasNoJobTypeProgress(job.type()) &&
                        (this.buildingJobs.isEmpty() || this.buildingJobs.getFirst()._id().equals(currentJobID)))
                    this.jobsService.onJobTypeProgress(job.type(), () ->
                            this.propertiesJobProgressComponent.incrementProgress());
            });
        });

        this.jobsService.onJobsLoadingFinished(() ->
                this.buildingJobs = this.jobsService.getJobObservableListOfType("building"));
    }

    public void showJobsPane() {
        this.jobProgressPane.setVisible(true);
        this.buildingCostsListView.setVisible(false);
        this.buyButton.setVisible(false);
        this.destroyButton.setVisible(false);
    }

    public void hideJobsPane() {
        this.jobProgressPane.setVisible(false);
        this.buildingCostsListView.setVisible(true);
        this.updateButtonStates();
        this.buyButton.setVisible(true);
        this.destroyButton.setVisible(true);
    }

    //Checks if buy and destroy building has to be deactivated
    public void disableButtons() {
        buyButton.setDisable(true);
        destroyButton.setDisable(true);
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), result -> {
            if (resourcesService.hasEnoughResources(result.cost())) buyButton.setDisable(false);
        });
        if (tokenStorage.getIsland().buildings().contains(buildingType)) destroyButton.setDisable(false);
    }

    public void destroyBuilding(){
        disableButtons();
        inGameController.handleDeleteStructure(buildingType);
    }

    //Gets called every second by a timer
    public void updateButtonStates(){
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), result -> {
            priceOfBuilding = result.cost();
            buyButton.setDisable(!resourcesService.hasEnoughResources(priceOfBuilding) ||
                    islandAttributeStorage.getUsedSlots() >= islandAttributeStorage.getIsland().resourceCapacity());
        });
        destroyButton.setDisable(!tokenStorage.getIsland().buildings().contains(buildingType));
    }

    //Timer for calling updateButtonStates every second
    public void startResourceMonitoring() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateButtonStates();
            }
        }, 0, 1000);
    }

    public void buyBuilding(){
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), result -> {
            priceOfBuilding = result.cost();
            if (resourcesService.hasEnoughResources(priceOfBuilding)) {
                this.subscriber.subscribe(this.jobsService.beginJob(
                        Jobs.createBuildingJob(this.tokenStorage.getIsland().id(), this.buildingType)), job -> {
                    this.currentJobID = job._id();
                    this.propertiesJobProgressComponent.setJobProgress(job);
                    this.showJobsPane();
                    this.updateIslandBuildings();

                    if (this.jobsService.hasNoJobTypeProgress(job.type()) &&
                            (this.buildingJobs.isEmpty() || this.buildingJobs.getFirst().equals(job)))
                        this.jobsService.onJobTypeProgress(job.type(), () ->
                                this.propertiesJobProgressComponent.incrementProgress());

                    this.jobsService.onJobDeletion(job._id(), () -> {
                        if (this.currentJobID.equals(job._id()))
                            this.getParent().setVisible(false);
                        this.updateIslandBuildings();
                    });
                    this.jobsService.onJobCompletion(job._id(), () -> {
                        this.updateIslandBuildings();
                        this.hideJobsPane();
                    });
                });
            } else buyButton.setDisable(true);
        });
    }

    private void updateIslandBuildings() {
        if (Objects.nonNull(this.islandAttributeStorage.getIsland())) {
            this.subscriber.subscribe(this.gameSystemsApiService.getSystem(
                    this.tokenStorage.getGameId(), this.islandAttributeStorage.getIsland().id()), island -> {
                this.tokenStorage.setIsland(this.islandsService.updateIsland(island));
                this.islandAttributeStorage.setIsland(this.islandsService.updateIsland(island));
                this.inGameController.islandsService.updateIslandBuildings(this.islandAttributeStorage,
                        this.inGameController, this.islandAttributeStorage.getIsland().buildings());
                this.inGameController.setSitePropertiesInvisible();
            }, error -> System.out.println("error here"));
        }
    }




    //Gets resources of the building and shows them in three listviews
    public void displayInfoBuilding(){
        Image imageBuilding = new Image(buildingsMap.get(buildingType));
        buildingImage.setImage(imageBuilding);
        buildingName.setText(gameResourceBundle.getString(buildingTranslation.get(buildingType)));
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), this::resourceListGeneration);

        buildingCostsListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "islandOverview", "building.costs"));
        buildingProducesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "islandOverview", "building.production"));
        buildingConsumesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "islandOverview", "building.upkeep"));
        disableButtons();
    }

    //Sets upkeep, production and cost of buildings in listviews
    private void resourceListGeneration(BuildingDto buildingDto) {
        Map<String, Integer> resourceMapUpkeep = buildingDto.upkeep();
        ObservableList<Resource> resourceListUpkeep = resourcesService.generateResourceList(resourceMapUpkeep, buildingConsumesListView.getItems(), null);
        buildingConsumesListView.setItems(resourceListUpkeep);

        Map<String, Integer> resourceMapProduce = buildingDto.production();
        ObservableList<Resource> resourceListProduce = resourcesService.generateResourceList(resourceMapProduce, buildingProducesListView.getItems(), null);
        buildingProducesListView.setItems(resourceListProduce);

        Map<String, Integer> resourceMapCost = buildingDto.cost();
        ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(resourceMapCost, buildingCostsListView.getItems(), null);
        buildingCostsListView.setItems(resourceListCost);
    }

    public void onClose(){
        setVisible(false);
    }
}
