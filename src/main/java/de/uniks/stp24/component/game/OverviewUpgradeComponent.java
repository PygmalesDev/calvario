package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.*;

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
    TokenStorage tokenStorage;
    @Inject
    ResourcesService resourcesService;

    private InGameController inGameController;
    private LinkedList<Text> resTextList;

    @Inject
    public OverviewUpgradeComponent() {

    }

    public void setUpgradeButton(){
        if(tokenStorage.getNeededResource() != null) {
            if (resourcesService.hasEnoughResources(tokenStorage.getNeededResource())) {
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

    public Map<Resource, Integer> setNeededResources(){
        resTextList = new LinkedList<>(Arrays.asList(res_1, res_2, res_3, res_4, report));
        Map<Resource, Integer> neededResources = new HashMap<>();
        int i = 0;
        switch(Upgrade.values()[inGameController.island.upgradeLevel()]){
            case unexplored, explored:
                for(Text res: resTextList){
                    res.setText("0");
                }
                break;
            case colonized:
                for (Map.Entry<String, Integer> entry : tokenStorage.getSystemPresets().colonized().cost().entrySet()) {
                    Resource resource = new Resource(entry.getKey(), entry.getValue(), 0); //TODO: Change "changePerSeason" later
                    neededResources.put(resource, resource.count());
                    tokenStorage.setNeededResources(neededResources);
                    resTextList.get(i).setText(entry.getKey() + " " + entry.getValue());
                    i += 1;
                }
                break;
            case upgraded:
                for (Map.Entry<String, Integer> entry : tokenStorage.getSystemPresets().upgraded().cost().entrySet()) {
                    Resource resource = new Resource(entry.getKey(), entry.getValue(), 0); //TODO: Change "changePerSeason" later
                    neededResources.put(resource, resource.count());
                    tokenStorage.setNeededResources(neededResources);
                    resTextList.get(i).setText(entry.getKey() + " " + entry.getValue());
                    i += 1;
                }
                break;
            case developed:
                for (Map.Entry<String, Integer> entry : tokenStorage.getSystemPresets().developed().cost().entrySet()) {
                    Resource resource = new Resource(entry.getKey(), entry.getValue(), 0); //TODO: Change "changePerSeason" later
                    neededResources.put(resource, resource.count());
                    tokenStorage.setNeededResources(neededResources);
                    resTextList.get(i).setText(entry.getKey() + " " + entry.getValue());
                    i += 1;
                }
                break;
        }
        return neededResources;
    }

    public void upgradeIsland(){
        resourcesService.upgradeIsland();
    }
}
