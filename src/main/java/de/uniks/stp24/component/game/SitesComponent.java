package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.Map;

@Component(view = "Sites.fxml")
public class SitesComponent extends VBox {

    @FXML
    public HBox sitesBox;

    InGameController inGameController;

    @Inject
    public SitesComponent() {

    }

    public void setSitesBox(Island island) {
        sitesBox.getChildren().clear();
        System.out.println(island);
        for (Map.Entry<String, Integer> entry : island.sites().entrySet()) {
            DistrictComponent districtComponent = new DistrictComponent(entry.getKey(), entry.getValue() + "/" + island.sitesSlots().get(entry.getKey()), inGameController);
            sitesBox.getChildren().add(districtComponent);
        }
    }


    public int getTotalSiteSlots(Island island){
        int totalSiteSlots = 0;
        for (Map.Entry<String, Integer> entry : island.sites().entrySet()) {
            totalSiteSlots = totalSiteSlots + entry.getValue();
        }
        return totalSiteSlots;
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }
}