package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.dto.SiteDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ResourcesService;
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
import org.controlsfx.control.GridView;
import org.fulib.fx.FulibFxApp;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;

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
    Subscriber subscriber;

    @Inject
    ResourcesService resourcesService;
    @Inject
    de.uniks.stp24.service.game.ResourcesService resourcesServiceGame;

    @Inject
    IslandsService islandsService;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    App app;


    @Inject
    public SitePropertiesComponent(){

    }

    private Island island;

    public ObservableList<Map<String, Integer>> resources;
    public ObservableList<ResourceComponent> resourceComponents;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, true, true, true, gameResourceBundle);


    @OnRender
    public void render(){
        this.siteType = "mining";
        siteName.setText(siteType);
    }

    @FXML
    public void initialize() {
        // Ensure resources list is initialized
        if (this.resourceComponents == null) {
            this.resourceComponents = FXCollections.observableArrayList();
        }
    }

    public void setSiteType(String siteType){
        this.siteType = siteType;
    }

    public void onClose(){
        setVisible(false);
    }

    public void buildSite(){
        island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.buildSite(tokenStorage.getGameId(), island, siteType), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
            displayAmountOfSite();
        });
    }

    public void destroySite(){
        if (tokenStorage.getIsland().sites().get(siteType) != 0){
            island = tokenStorage.getIsland();
            subscriber.subscribe(resourcesService.destroySite(tokenStorage.getGameId(), island, siteType), result -> {
                tokenStorage.setIsland(islandsService.updateIsland(result));
                displayAmountOfSite();
            });
        }
    }
    @OnRender
    public void displayCostsOfSite(){
        siteCostsListView.setSelectionModel(null);
        subscriber.subscribe(resourcesService.getResourcesSite(siteType), this::resourceListGeneration);
        siteConsumesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        siteCostsListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        siteProducesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
    }

    public void displayAmountOfSite(){
        int amountSite = 0;
        int amountSiteSlots = 0;
        if (tokenStorage.getIsland().sites().get(siteType) != null){
             amountSite = tokenStorage.getIsland().sites().get(siteType);
        }
        if (tokenStorage.getIsland().sitesSlots().get(siteType) != null){
            amountSiteSlots= tokenStorage.getIsland().sitesSlots().get(siteType);
        }

        int count = 0;
        System.out.println("ccccc " + tokenStorage.getIsland().sites().get(siteType));

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
                imageViewEmpty.setFitWidth(30);
                imageViewEmpty.setFitHeight(12);

                imageViewFilled.setFitWidth(30);
                imageViewFilled.setFitHeight(12);

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
        ObservableList<Resource> resourceListPrice = resourcesServiceGame.generateResourceList(resourceMapPrice, siteCostsListView.getItems());
        siteCostsListView.setItems(resourceListPrice);

        Map<String, Integer> resourceMapUpkeep = siteDto.upkeep();
        ObservableList<Resource> resourceListUpkeep = resourcesServiceGame.generateResourceList(resourceMapUpkeep, siteConsumesListView.getItems());
        siteConsumesListView.setItems(resourceListUpkeep);

        Map<String, Integer> resourceMapProduce = siteDto.production();
        ObservableList<Resource> resourceListProduce = resourcesServiceGame.generateResourceList(resourceMapProduce, siteProducesListView.getItems());
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




