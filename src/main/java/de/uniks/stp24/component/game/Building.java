package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;

@Component(view = "BuildingElement.fxml")
public class Building extends VBox {
    @FXML
    private ImageView building;
    InGameController inGameController;

    private String buildingType;


    public Building(BuildingsComponent buildingsComponent, String buildingName, TokenStorage tokenStorage, IslandAttributeStorage islandAttributes, InGameController inGameController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BuildingElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.inGameController = inGameController;

        building.setOnMouseClicked(event -> {
            inGameController.showBuildingWindow();
            //TODO: Need to be modified for game
            if(buildingName.equals("empty") && Objects.equals(tokenStorage.getEmpireId(), islandAttributes.getIsland().owner()) && islandAttributes.getUsedSlots() < islandAttributes.getIsland().resourceCapacity()) {
                //TODO: Logic for editing new Building son page(gridpane)
                buildingsComponent.islandAttributes.addNewBuilding();
                int size = buildingsComponent.islandAttributes.getIsland().buildings().size();
                buildingsComponent.islandAttributes.getIsland().buildings().set(size - 1, String.valueOf(size));
                buildingsComponent.setGridPane();
                //Increase res capacity in a dynamic way
                this.inGameController.overviewSitesComponent.setOverviewSites();
            }
        });
    }

    @Inject
    public Building(){

    }
//    public void setInGameController(InGameController inGameController){
//        this.inGameController = inGameController;
//    }


    public void setBuildingType(String buildingType){
        this.buildingType = buildingType;
    }

}
