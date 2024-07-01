package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static de.uniks.stp24.service.Constants.sitesIconPathsMap;

@Component(view = "DistrictComponent.fxml")
public class DistrictComponent extends VBox {
    @FXML
    public Text districtCapacity;
    @FXML
    Button siteElement;
    Map<String, String> sitesMap;



    @Inject
    public DistrictComponent(String name, String capacity, InGameController inGameController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DistrictComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sitesMap = sitesIconPathsMap;
        String imagePath;
        if (sitesMap.get(name) == null){
            imagePath = "de/uniks/stp24/icons/sites/production_site.png";
        } else {
            imagePath = sitesMap.get(name);
        }

        siteElement.setDisable(inGameController.tokenStorage.isSpectator() || !Objects.equals(inGameController.islandAttributes.getIsland().owner(), inGameController.tokenStorage.getEmpireId()));

        siteElement.setStyle("-fx-background-image: url('/" + imagePath + "'); " +
                "-fx-background-size: 100% 100%;" + "-fx-background-color: transparent;" + "-fx-background-repeat: no-repeat;");

        districtCapacity.setText(capacity);

        siteElement.setOnMouseClicked(event -> {
            inGameController.showSiteOverview();
            inGameController.setSiteType(name);
            inGameController.selectedSites = name;
        });
    }

}
