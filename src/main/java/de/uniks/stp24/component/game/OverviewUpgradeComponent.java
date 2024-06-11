package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {
    @FXML
    public Text report;
    @FXML
    public Text res_4;
    @FXML
    public Text res_3;
    @FXML
    public Text res_2;
    @FXML
    public Text res_1;
    @FXML
    public Pane confirmUpgrade;
    @Inject
    InGameService inGameService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    IslandAttributeStorage islandAttributes;

    private InGameController inGameController;

    @Inject
    public OverviewUpgradeComponent() {

    }

    public void setUpgradeButton(){
        if(islandAttributes.getNeededResources(inGameController.island.upgradeLevel()) != null) {
            if (resourcesService.hasEnoughResources(islandAttributes.getNeededResources(inGameController.island.upgradeLevel()))) {
                confirmUpgrade.setStyle("-fx-background-color: green;");
            } else {
                confirmUpgrade.setStyle("-fx-background-color: black;");
            }
        }
    }

    public void goBack() {
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewSitesComponent);
    }

    public void closeOverview() {
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.rudderImage.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;
        if (inGameController.island.flagIndex() >= 0) {
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
        }
        inGameController.selectedIsland = null;
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void setNeededResources(){
        if(inGameController != null) {
            LinkedList<Text> resTextList = new LinkedList<>(Arrays.asList(res_1, res_2, res_3, res_4, report));
            int i = 0;
            for (Map.Entry<String, Integer> entry : islandAttributes.getNeededResources(inGameController.island.upgradeLevel()).entrySet()) {
                resTextList.get(i).setText(entry.getKey() + " " + entry.getValue());
                i += 1;
            }
        }
    }

    public void upgradeIsland(){
        System.out.println(resourcesService.hasEnoughResources(islandAttributes.getNeededResources(inGameController.island.upgradeLevel())));
        if(resourcesService.hasEnoughResources(islandAttributes.getNeededResources(inGameController.island.upgradeLevel()))) {
            resourcesService.upgradeIsland();
        }
    }
}
