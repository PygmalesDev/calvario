package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "Sites.fxml")
public class SitesComponent extends VBox {

    @FXML
    public HBox sitesBox;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    InGameController inGameController;

    @Inject
    public SitesComponent() {

    }

    /*
    The methods below showing sites dependent on amount of sites on island.
     */

    public void setSitesBox(Island island) {
        sitesBox.getChildren().clear();
        VBox vBox = new VBox();
        vBox.setPrefWidth(10);
        sitesBox.getChildren().add(vBox);
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