package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Arrays;

@Component(view = "BuildingsWindow.fxml")
public class BuildingsWindowComponent extends AnchorPane {
    @FXML
    Button buildingRefinery;
    @FXML
    Button buildingFactory;
    @FXML
    Button buildingFoundry;
    @FXML
    Button buildingResearchLab;
    @FXML
    Button buildingFarm;
    @FXML
    Button buildingMine;
    @FXML
    Button buildingPowerPlant;
    @FXML
    Button buildingExchange;
    @FXML
    Button closeWindowButton;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    Subscriber subscriber;

    @Inject
    IslandsService islandsService;

    @Inject
    ResourcesService resourcesService;

    private Island island;

    private String buildingToAdd;

    Image image;

    ImageView imageView;

    private Button[] buttons;

    private static final String[] IMAGE_PATHS = {
            "de/uniks/stp24/icons/buildings/alloy_smeltery.png",
            "de/uniks/stp24/icons/buildings/theurgy_hall.png",
            "de/uniks/stp24/icons/buildings/chophouse.png",
            "de/uniks/stp24/icons/buildings/resonating_delves.png",
            "de/uniks/stp24/icons/buildings/farmside.png",
            "de/uniks/stp24/icons/buildings/coal_querry.png",
            "de/uniks/stp24/icons/buildings/scout_hub.png",
            "de/uniks/stp24/icons/buildings/seaside_hut.png",
    };



    @Inject
    public BuildingsWindowComponent(){

    }

    @FXML
    public void initialize() {
        buttons = new Button[]{
                buildingRefinery,
                buildingFactory,
                buildingFoundry,
                buildingResearchLab,
                buildingFarm,
                buildingMine,
                buildingPowerPlant,
                buildingExchange
        };
    }

    @OnRender
    public void setImages() {
        for (int i = 0; i < buttons.length; i++) {
            Image image = new Image(IMAGE_PATHS[i]);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(60);
            imageView.setFitHeight(60);
            buttons[i].setGraphic(imageView);
            buttons[i].getStyleClass().clear();
        }
    }

    public void buildExchange(){
        this.buildingToAdd = "exchange";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void buildPowerPlant(){
        this.buildingToAdd = "power_plant";
        this.island = tokenStorage.getIsland();

        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void buildMine(){
        this.buildingToAdd = "mine";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }


    public void buildFarm(){
        this.buildingToAdd = "farm";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void buildResearchLab(){
        this.buildingToAdd = "research_lab";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void buildFoundry(){
        this.buildingToAdd = "foundry";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void buildFactory(){
        this.buildingToAdd = "factory";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void buildRefinery(){
        this.buildingToAdd = "refinery";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            tokenStorage.setIsland(islandsService.updateIsland(result));
        },
                error -> System.out.println("Insufficient funds"));
    }

    public void onClose(){
        setVisible(false);
    }
}
