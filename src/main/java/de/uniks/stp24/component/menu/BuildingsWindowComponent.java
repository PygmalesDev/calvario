package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.ResourcesService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.concurrent.Flow;

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
    ResourcesService resourcesService;

    private Island island;

    private String buildingToAdd;


    @Inject
    public BuildingsWindowComponent(){

    }

    public void buildExchange(){
        this.buildingToAdd = "exchange";
        this.island = tokenStorage.getIsland();
        subscriber.subscribe(resourcesService.createBuilding(tokenStorage.getGameId(), island, buildingToAdd), result -> {
            onClose();
        });
    }

    public void buildPowerPlant(){

    }

    public void buildMine(){

    }

    public void buildFarm(){

    }

    public void buildResearchLab(){

    }

    public void buildFoundry(){

    }

    public void buildFactory(){

    }

    public void buildRefinery(){

    }

    public void onClose(){
        setVisible(false);
    }
}
