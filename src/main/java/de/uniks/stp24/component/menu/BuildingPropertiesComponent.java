package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.dto.BuildingDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import net.bytebuddy.description.ByteCodeElement;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.*;

@Component(view = "BuildingProperties.fxml")
public class BuildingPropertiesComponent extends AnchorPane {

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
    de.uniks.stp24.service.game.ResourcesService resourcesServiceGame;

    @Inject
    App app;

    @SubComponent
    @Inject
    public LobbyHostSettingsComponent lobbyHostSettingsComponent;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;


    @Inject
    TokenStorage tokenStorage;

    public List<Island> islands = new ArrayList<>();

    Map<String, String> buildingsMap;

    String buildingType;

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
        String buildingMine = "mine";
        setBuildingType(buildingMine);
    }

    @Inject
    public BuildingPropertiesComponent(){

    }

    public void setIsland(Island island){

    }

    public void setBuildingType(String buildingType){
        this.buildingType = buildingType;
    }

    public void destroyBuilding(){
        Island island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.destroyBuilding(tokenStorage.getGameId(), island), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
            onClose();
        });
    }
    @OnRender
    public void displayInfoBuilding(){
        Image imageBuilding = new Image(buildingsMap.get(buildingType));
        buildingImage.setImage(imageBuilding);
        buildingName.setText(buildingType.toUpperCase());
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), this::resourceListGeneration);
        buildingProducesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
        buildingConsumesListView.setCellFactory(list -> new CustomComponentListCell<>(app, resourceComponentProvider));
    }

    private void resourceListGeneration(BuildingDto buildingDto) {
        Map<String, Integer> resourceMapUpkeep = buildingDto.upkeep();
        ObservableList<Resource> resourceListUpkeep = resourcesServiceGame.generateResourceList(resourceMapUpkeep, buildingConsumesListView.getItems());
        buildingConsumesListView.setItems(resourceListUpkeep);

        Map<String, Integer> resourceMapProduce = buildingDto.production();
        ObservableList<Resource> resourceListProduce = resourcesServiceGame.generateResourceList(resourceMapProduce, buildingProducesListView.getItems());
        buildingProducesListView.setItems(resourceListProduce);
    }

    public void onClose(){
        setVisible(false);
    }
}
