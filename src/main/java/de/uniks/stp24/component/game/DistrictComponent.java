package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        String imagePath;
        if (sitesMap.get(name) == null){
            imagePath = "de/uniks/stp24/icons/sites/production_site.png";
        } else {
            imagePath = sitesMap.get(name);
        }

        if(inGameController.tokenStorage.isSpectator() || !Objects.equals(inGameController.islandAttributes.getIsland().owner(), inGameController.tokenStorage.getEmpireId())) {
            siteElement.setDisable(true);
        } else {
            siteElement.setDisable(false);
        }

        siteElement.setStyle("-fx-background-image: url('/" + imagePath + "'); " +
                "-fx-background-size: 100% 100%;" + "-fx-background-color: transparent;" + "-fx-background-repeat: no-repeat;");

        districtCapacity.setText(capacity);

        siteElement.setOnMouseClicked(event -> {
            inGameController.showSiteOverview();
            inGameController.setSiteType(name);
        });
    }

}
