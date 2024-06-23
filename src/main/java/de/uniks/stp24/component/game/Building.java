package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component(view = "BuildingElement.fxml")
public class Building extends VBox {
    @FXML
    private Button building;
    InGameController inGameController;

    private ImageView imageView;


    public Building(BuildingsComponent buildingsComponent, String buildingName, TokenStorage tokenStorage, IslandAttributeStorage islandAttributes, InGameController inGameController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BuildingElement.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        System.out.println(buildingName + " BUIULDINGNAME");

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        building.setDisable(inGameController.tokenStorage.isSpectator() || !Objects.equals(islandAttributes.getIsland().owner(), inGameController.tokenStorage.getEmpireId()));
        building.getStyleClass().clear();
        this.inGameController = inGameController;
        Map<String, String> buildingsMap = new HashMap<>();
        buildingsMap.put("refinery", "de/uniks/stp24/icons/buildings/alloy_smeltery.png");
        buildingsMap.put("factory", "de/uniks/stp24/icons/buildings/theurgy_hall.png");
        buildingsMap.put("foundry", "de/uniks/stp24/icons/buildings/chophouse.png");
        buildingsMap.put("research_lab", "de/uniks/stp24/icons/buildings/resonating_delves.png");
        buildingsMap.put("farm", "de/uniks/stp24/icons/buildings/farmside.png");
        buildingsMap.put("mine", "de/uniks/stp24/icons/buildings/coal_querry.png");
        buildingsMap.put("power_plant", "de/uniks/stp24/icons/buildings/scout_hub.png");
        buildingsMap.put("exchange", "de/uniks/stp24/icons/buildings/seaside_hut.png");
        if (buildingsMap.get(buildingName) != null){
            Image image = new Image(buildingsMap.get(buildingName));
            imageView = new ImageView(image);
            imageView.setFitWidth(40); // Set the width to fit the button
            imageView.setFitHeight(40); // Set the height to fit the button
            building.setGraphic(imageView);
        } else {
            Image image = new Image("de/uniks/stp24/icons/buildings/empty_building_element.png");
            imageView = new ImageView(image);
            imageView.setFitWidth(40); // Set the width to fit the button
            imageView.setFitHeight(40); // Set the height to fit the button
            building.setGraphic(imageView);
        }


        building.setOnMouseClicked(event -> {
            String imageUrl = imageView.getImage().getUrl();
            String relevantPart = extractRelevantPath(imageUrl);
            if (imageView != null && imageView.getImage() != null && !relevantPart.equals("/de/uniks/stp24/icons/buildings/empty_building_element.png")){
                inGameController.buildingsWindowComponent.setVisible(false);
                inGameController.setSitePropertiesInvisible();
                inGameController.showBuildingInformation(buildingName);

            } else {
                inGameController.showBuildingWindow();
                if(buildingName.equals("buildNewBuilding") && Objects.equals(tokenStorage.getEmpireId(), islandAttributes.getIsland().owner()) && islandAttributes.getUsedSlots() < islandAttributes.getIsland().resourceCapacity()) {
                    //buildingsComponent.islandAttributes.addNewBuilding();
                    buildingsComponent.setGridPane();
                    //inGameController.islandsService.updateIslandBuildings(islandAttributes, inGameController, islandAttributes.getIsland().buildings());
                }
            }

        });


    }

    private String extractRelevantPath(String imageUrl) {
        int index = imageUrl.indexOf("/de/uniks/stp24/");
        if (index != -1) {
            return imageUrl.substring(index);
        } else {
            return imageUrl; // Return the whole URL if the relevant part is not found
        }
    }

    @Inject
    public Building(){

    }
}
