package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.SiteDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.ExplanationService;
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
    ExplanationService explanationService;
    @Inject
    App app;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle);

    @Inject
    public SitePropertiesComponent(){
    }
    Map<String, String> sitesMap;

    private int amountSite = 0;
    private int amountSiteSlots = 0;

    public ObservableList<Map<String, Integer>> resources;
    public ObservableList<ResourceComponent> resourceComponents;

    InGameController inGameController;



    @OnInit
    public void init(){
        System.out.println(gameResourceBundle != null);
        sitesMap = sitesIconPathsMap;
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
        Island island = tokenStorage.getIsland();
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
        siteConsumesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "islandOverview"));
        siteCostsListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "islandOverview"));
        siteProducesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "islandOverview"));
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

}

