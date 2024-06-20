package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.BuildingDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.*;

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

    @Inject
    ResourcesService resourcesService;

    @Inject
    Subscriber subscriber;

    @Inject
    IslandsService islandsService;


    @Inject
    App app;


    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;


    @Inject
    TokenStorage tokenStorage;

    @Inject
    IslandAttributeStorage islandAttributeStorage;

    Map<String, String> buildingsMap;

    String buildingType;
    InGameController inGameController;
    Map<String, Integer> priceOfBuilding;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, true, true, true, gameResourceBundle);


    @OnInit
    public void init(){
        buildingsMap = new HashMap<>();
        buildingsMap.put("refinery", "de/uniks/stp24/icons/buildings/alloy_smeltery.png");
        buildingsMap.put("factory", "de/uniks/stp24/icons/buildings/theurgy_hall.png");
        buildingsMap.put("foundry", "de/uniks/stp24/icons/buildings/chophouse.png");
        buildingsMap.put("research_lab", "de/uniks/stp24/icons/buildings/resonating_delves.png");
        buildingsMap.put("farm", "de/uniks/stp24/icons/buildings/farmside.png");
        buildingsMap.put("mine", "de/uniks/stp24/icons/buildings/coal_querry.png");
        buildingsMap.put("power_plant", "de/uniks/stp24/icons/buildings/scout_hub.png");
        buildingsMap.put("exchange", "de/uniks/stp24/icons/buildings/seaside_hut.png");

    }

    @Inject
    public BuildingPropertiesComponent(){

    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }


    public void setBuildingType(String buildingType){
        this.buildingType = buildingType;
        displayInfoBuilding();
        disableButtons();
    }

    public void disableButtons(){
        buyButton.setDisable(true);
        destroyButton.setDisable(true);
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), result -> {
            priceOfBuilding = result.cost();
            if (resourcesService.hasEnoughResources(priceOfBuilding)) {
                buyButton.setDisable(false);
            }
        });
        if (tokenStorage.getIsland().buildings().contains(buildingType)){
            destroyButton.setDisable(false);
        }

    }

    public void destroyBuilding(){
        disableButtons();
        inGameController.handleDeleteStructure(buildingType);
    }

    public void buyBuilding(){
        Island island = tokenStorage.getIsland();

        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingType), result -> {
                    tokenStorage.setIsland(islandsService.updateIsland(result));
                    islandAttributeStorage.setIsland(islandsService.updateIsland(result));
                },
                error -> buyButton.setDisable(true));

        disableButtons();
    }


    public void displayInfoBuilding(){

        Image imageBuilding = new Image(buildingsMap.get(buildingType));
        buildingImage.setImage(imageBuilding);
        buildingName.setText(buildingType.toUpperCase());
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), this::resourceListGeneration);
        buildingCostsListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        buildingProducesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        buildingConsumesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        disableButtons();
    }

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
