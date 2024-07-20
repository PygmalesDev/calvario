package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.SiteDto;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.fulib.fx.FulibFxApp;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "SiteProperties.fxml")
public class SitePropertiesComponent extends AnchorPane {
    @FXML
    GridPane siteAmountGridPane;
    @FXML
    ListView<Resource> siteCostsListView;
    @FXML
    ListView<Resource> siteProducesListView;
    @FXML
    ListView<Resource> siteConsumesListView;
    @FXML
    Button buildSiteButton;
    @FXML
    Button destroySiteButton;
    @FXML
    ImageView siteImage;
    @FXML
    Button closeWindowButton;
    @FXML
    Text siteName;
    @FXML
    AnchorPane jobPane;
    @FXML
    ScrollPane siteAmountScrollPane;

    String siteType;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    IslandAttributeStorage islandAttributeStorage;

    @Inject
    Subscriber subscriber;

    @Inject
    JobsService jobsService;


    @Inject
    ResourcesService resourcesService;
    @Inject
    ImageCache imageCache;

    @Inject
    IslandsService islandsService;

    @Inject
    App app;

    @Inject
    GameSystemsApiService gameSystemsApiService;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    @SubComponent
    public PropertiesJobProgressComponent siteJobProgress;

    Provider<ResourceComponent> negativeResourceProvider = () -> new ResourceComponent("negative", gameResourceBundle);
    Provider<ResourceComponent> positiveResourceProvider = () -> new ResourceComponent("positive", gameResourceBundle);
    Provider<ImageView> siteEmptyCellProvider = () -> {
        ImageView imageView = new ImageView(this.imageCache.get("/de/uniks/stp24/icons/other/empty_building_small_element.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        return imageView;
    };

    Provider<ImageView> siteCellProvider = () -> {
        ImageView imageView = new ImageView(this.imageCache.get("/de/uniks/stp24/icons/other/building_small_element.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        return imageView;
    };

    Map<String, String> sitesMap;

    private int amountSite = 0;
    private int amountSiteSlots = 0;

    public ObservableList<Map<String, Integer>> resources;
    public ObservableList<ResourceComponent> resourceComponents;
    private ObservableList<Job> siteJobs;

    InGameController inGameController;

    @Inject
    public SitePropertiesComponent() {}

    @OnInit
    public void init(){
        sitesMap = sitesIconPathsMap;
    }

    @OnRender
    public void render() {
        this.jobPane.getChildren().add(this.siteJobProgress);
        this.setPickOnBounds(false);
        this.jobPane.setPickOnBounds(false);
        this.jobPane.setVisible(false);
    }

    @OnRender
    public void addRunnable() {
        // this method will be run after resources update themselves, to (dis)-enable buttons dynamically
        resourcesService.setOnResourceUpdates(this::setButtonsDisable);
    }

    @FXML
    public void initialize() {
        // Ensure resources list is initialized
        if (this.resourceComponents == null) {
            this.resourceComponents = FXCollections.observableArrayList();
        }
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }


    public void setSiteType(String siteType){
        this.siteType = siteType;
        setButtonsDisable();

        siteName.setText(gameResourceBundle.getString(siteTranslation.get(siteType)));
        Image imageSite = new Image(sitesMap.get(siteType));
        siteImage.getStyleClass().clear();
        siteImage.setImage(imageSite);
        displayCostsOfSite();
        displayAmountOfSite();

        if (this.siteJobs.stream().anyMatch(job -> job.district().equals(siteType)
                && job.system().equals(this.tokenStorage.getIsland().id()))) {
            Job job = this.siteJobs.stream().filter(started -> started.district().equals(siteType)
                    && started.system().equals(this.tokenStorage.getIsland().id()))
                    .findFirst().orElse(null);
            this.showJobsPane();
            if (Objects.nonNull(job)) {
                this.siteJobProgress.setJobProgress(job);
                if (this.jobsService.hasNoJobTypeProgress(job.type()) && this.siteJobs.get(0).equals(job))
                    this.jobsService.onJobTypeProgress(job.type(), () -> this.siteJobProgress.incrementProgress());
            }
        } else {
            this.hideJobsPane();
            this.jobsService.stopOnJobTypeProgress("district");
        }


    }

    public void onClose(){
        setVisible(false);
    }

    public void buildSite(){
        this.subscriber.subscribe(this.jobsService.beginJob(Jobs.createDistrictJob(
                this.tokenStorage.getIsland().id(), this.siteType)), job ->  {
            this.showJobsPane();
            this.siteJobProgress.setJobProgress(job);

            if (this.jobsService.hasNoJobTypeProgress(job.type()) &&
                    (this.siteJobs.isEmpty() || this.siteJobs.getFirst().equals(job)))
                this.jobsService.onJobTypeProgress(job.type(), () -> this.siteJobProgress.incrementProgress());

            this.jobsService.onJobDeletion(job._id(), () -> {
                if (job.district().equals(this.siteType)) this.hideJobsPane();
            });
            this.jobsService.onJobCompletion(job._id(), () -> {
                if (job.district().equals(this.siteType)) this.hideJobsPane();
            });
        });
    }

    @OnInit
    public void setSitesJobUpdates() {
        this.jobsService.onJobsLoadingFinished("district", job -> {
            this.jobsService.onJobDeletion(job._id(), () -> {
                if (job.district().equals(this.siteType)) this.hideJobsPane();
            });
            this.jobsService.onJobCompletion(job._id(), () -> {
                this.hideJobsPane();
            });
        });

        this.jobsService.onJobsLoadingFinished(() ->
                this.siteJobs = this.jobsService.getJobObservableListOfType("district"));
    }


    public void showJobsPane() {
        this.jobPane.setVisible(true);
        this.siteCostsListView.setVisible(false);
    }

    public void hideJobsPane() {
        this.jobPane.setVisible(false);
        this.siteCostsListView.setVisible(true);
    }

    //Calls handleDeleteStructure in inGameController which shows the deleteWarning popup
    //and calls method in DeleteStructureComponent
    public void destroySite(){
        inGameController.handleDeleteStructure(siteType);
    }

    //Gets resources of site and displays them in listviews
    public void displayCostsOfSite(){
        siteCostsListView.setSelectionModel(null);
        if (Objects.nonNull(siteType))
            subscriber.subscribe(resourcesService.getResourcesSite(siteType), this::resourceListGeneration,
                    error -> System.out.println("error displayCostsOfSite(): " + error));
        siteConsumesListView.setCellFactory(list -> new CustomComponentListCell<>(app, negativeResourceProvider));
        siteCostsListView.setCellFactory(list -> new CustomComponentListCell<>(app, negativeResourceProvider));
        siteProducesListView.setCellFactory(list -> new CustomComponentListCell<>(app, positiveResourceProvider));
        if (Objects.nonNull(siteType))
            subscriber.subscribe(resourcesService.getResourcesSite(siteType), this::resourceListGeneration,
                    error -> System.out.println("error displayCostsOfSite(): " + error));
    }

    //Uses a GridPane to display a graphic view of how many sites of each type you have
    public void displayAmountOfSite(){
        buildSiteButton.setDisable(false);
        destroySiteButton.setDisable(false);
        setButtonsDisable();

        amountSite = Objects.nonNull(tokenStorage.getIsland().sites().get(siteType)) ?
                tokenStorage.getIsland().sites().get(siteType) : 0;
        amountSiteSlots = tokenStorage.getIsland().sitesSlots().get(siteType);

        siteAmountScrollPane.setVvalue(0);
        siteAmountGridPane.getChildren().clear();
        siteAmountGridPane.getRowConstraints().clear();
        siteAmountGridPane.addRow(0);

        int x = 0, y = 0, slots = 0, builtSlots = 0;
        while (slots != amountSiteSlots) {
            if (builtSlots < amountSite) siteAmountGridPane.add(siteCellProvider.get(), x, y);
            else siteAmountGridPane.add(siteEmptyCellProvider.get(), x, y);

            slots++;
            builtSlots++;
            x++;
            if (x == 5) {
                x = 0;
                y++;
                siteAmountGridPane.addRow(y);
            }
        }

        buildSiteButton.setDisable(amountSiteSlots == amountSite);
        destroySiteButton.setDisable(amountSite == 0);
    }

    private void setButtonsDisable() {
        // check if empire has enough resources to build a site cell
        if (Objects.nonNull(siteType))
            subscriber.subscribe(resourcesService.getResourcesSite(siteType), result -> {
                Map<String, Integer> costSite = result.cost();
                if (!resourcesService.hasEnoughResources(costSite)){
                    buildSiteButton.setDisable(true);
                }
        }, error -> System.out.println("error setButtonsDisable(): " + error));
    }

    private void resourceListGeneration(SiteDto siteDto) {

        Map<String, Integer> resourceMapPrice = siteDto.cost();
        ObservableList<Resource> resourceListPrice = resourcesService.generateResourceList(resourceMapPrice, siteCostsListView.getItems(),null);
        siteCostsListView.setItems(resourceListPrice);

        Map<String, Integer> resourceMapUpkeep = siteDto.upkeep();
        ObservableList<Resource> resourceListUpkeep = resourcesService.generateResourceList(resourceMapUpkeep, siteConsumesListView.getItems(), null);
        siteConsumesListView.setItems(resourceListUpkeep);

        Map<String, Integer> resourceMapProduce = siteDto.production();
        ObservableList<Resource> resourceListProduce = resourcesService.generateResourceList(resourceMapProduce, siteProducesListView.getItems(), null);
        siteProducesListView.setItems(resourceListProduce);
    }

}

class CustomComponentListCell<Item, Component extends Parent> extends ListCell<Item> {

    private final FulibFxApp app;
    private final Provider<? extends Component> provider;
    private final Map<String, Object> extraParams; // extra parameters to pass to the component

    private Component component;

    /**
     * Creates a new component list cell.
     *
     * @param app      The FulibFX app
     * @param provider The provider to create the component
     */
    public CustomComponentListCell(FulibFxApp app, Provider<? extends Component> provider) {
        this(app, provider, Map.of());
    }

    /**
     * Creates a new component list cell.
     *
     * @param app         The FulibFX app
     * @param provider    The provider to create the component
     * @param extraParams Extra parameters to pass to the component
     */
    public CustomComponentListCell(FulibFxApp app, Provider<? extends Component> provider, Map<String, Object> extraParams) {
        super();
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.app = app;
        this.provider = provider;
        this.extraParams = extraParams;
    }

    @Override
    protected void updateItem(Item item, boolean empty) {
        super.updateItem(item, empty);
        setPrefHeight(5);
        setPrefWidth(50);
        // Destroy component if the cell is emptied
        if (empty || item == null) {
            if (component != null) {
                app.destroy(component);
                component = null;
            }
            setGraphic(null);
            return;
        }

        // Destroy old component if necessary (if it is not reusable)
        if (component != null && !(component instanceof ReusableItemComponent<?>)) {
            app.destroy(component);
            component = null;
        }

        // Create and render new component if necessary
        if (component == null) {
            component = provider.get();
            // Add item and list to parameters if they are not already present
            final Map<String, Object> params = new HashMap<>(extraParams);
            params.putIfAbsent("item", item);
            params.putIfAbsent("list", getListView().getItems());
            setGraphic(app.initAndRender(component, params));
        }

        // Update component if possible
        if (component instanceof ReusableItemComponent<?>) {
            //noinspection unchecked
            ((ReusableItemComponent<Item>) component).setItem(item);
        }
    }
}