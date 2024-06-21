package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.SiteDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.fulib.fx.FulibFxApp;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.siteTranslation;

@Component(view = "SiteProperties.fxml")
public class SitePropertiesComponent extends AnchorPane {
    @FXML
    Button activateGridButton;
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

    String siteType;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    IslandAttributeStorage islandAttributeStorage;

    @Inject
    Subscriber subscriber;

    @Inject
    ResourcesService resourcesService;

    @Inject
    IslandsService islandsService;

    @Inject
    App app;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;


    @Inject
    public SitePropertiesComponent(){

    }
    Map<String, String> sitesMap;

    private int amountSite = 0;
    private int amountSiteSlots = 0;
    private Island island;

    public ObservableList<Map<String, Integer>> resources;
    public ObservableList<ResourceComponent> resourceComponents;

    InGameController inGameController;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle);

    @OnInit
    public void init(){
        sitesMap = new HashMap<>();
        sitesMap.put("city", "de/uniks/stp24/icons/sites/village_site.png");
        sitesMap.put("energy", "de/uniks/stp24/icons/sites/thaumaturgy_site.png");
        sitesMap.put("mining", "de/uniks/stp24/icons/sites/mining_site.png");
        sitesMap.put("agriculture", "de/uniks/stp24/icons/sites/harvesting_site.png");
        sitesMap.put("industry", "de/uniks/stp24/icons/sites/coalmine_site.png");
        sitesMap.put("research_site", "de/uniks/stp24/icons/sites/epoch_site.png");
        sitesMap.put("ancient_foundry", "de/uniks/stp24/icons/sites/expedition_site.png");
        sitesMap.put("ancient_factory", "de/uniks/stp24/icons/sites/merchant_site.png");
        sitesMap.put("ancient_refinery", "de/uniks/stp24/icons/sites/production_site.png");
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
        siteName.setText(gameResourceBundle.getString(siteTranslation.get(siteType)));
        Image imageSite = new Image(sitesMap.get(siteType));
        siteImage.getStyleClass().clear();
        siteImage.setImage(imageSite);
        displayCostsOfSite();
        displayAmountOfSite();

    }

    public void onClose(){
        setVisible(false);
    }

    public void buildSite(){
        island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.buildSite(tokenStorage.getGameId(), island, siteType), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
            islandAttributeStorage.setIsland(islandsService.updateIsland(result));

            displayAmountOfSite();
            inGameController.updateSiteCapacities();
        });


    }

    public void destroySite(){
        inGameController.handleDeleteStructure(siteType);

    }

    public void displayCostsOfSite(){
        siteCostsListView.setSelectionModel(null);
        subscriber.subscribe(resourcesService.getResourcesSite(siteType), this::resourceListGeneration);
        inGameController.updateSiteCapacities();
        siteConsumesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        siteCostsListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        siteProducesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
    }

    public void displayAmountOfSite(){
        buildSiteButton.setDisable(false);
        destroySiteButton.setDisable(false);
        subscriber.subscribe(resourcesService.getResourcesSite(siteType), result -> {
            Map<String, Integer> costSite = result.cost();
            if (!resourcesService.hasEnoughResources(costSite)){
                buildSiteButton.setDisable(true);
            }
        });

        if (tokenStorage.getIsland().sites().get(siteType) != null){
            amountSite = tokenStorage.getIsland().sites().get(siteType);
        }
        if (tokenStorage.getIsland().sitesSlots().get(siteType) != null){
            amountSiteSlots= tokenStorage.getIsland().sitesSlots().get(siteType);
        }
        if (amountSiteSlots == amountSite){
            buildSiteButton.setDisable(true);
        }

        if (amountSite == 0){
            destroySiteButton.setDisable(true);
        }

        int count = 0;

        // Clear the existing children in the GridPane
        siteAmountGridPane.getChildren().clear();

        // Create an Image object
        Image emptySlot = new Image("de/uniks/stp24/icons/other/empty_building_small_element.png");
        Image filledSlot = new Image("de/uniks/stp24/icons/other/building_small_element.png");

        // Loop through each cell in the 6x6 grid
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                // Create an ImageView for each cell
                ImageView imageViewEmpty = new ImageView(emptySlot);
                ImageView imageViewFilled = new ImageView(filledSlot);
                imageViewEmpty.setFitWidth(20);
                imageViewEmpty.setFitHeight(20);

                imageViewFilled.setFitWidth(20);
                imageViewFilled.setFitHeight(20);

                // Add the ImageView to the GridPane
                if (count < amountSite){
                    siteAmountGridPane.add(imageViewFilled, col, row);
                } else {
                    siteAmountGridPane.add(imageViewEmpty, col, row);
                }

                imageViewEmpty.setVisible(false);

                if (amountSiteSlots > amountSite){
                    if (count >= amountSite && count < amountSiteSlots){
                        imageViewEmpty.setVisible(true);
                    }
                }
                count++;
            }
        }

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

    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
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