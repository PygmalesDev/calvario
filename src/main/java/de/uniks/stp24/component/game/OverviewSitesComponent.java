package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Component(view = "IslandOverviewSites.fxml")
public class OverviewSitesComponent extends AnchorPane {

    @FXML
    public Button detailsButton;
    @FXML
    public Button sitesButton;
    @FXML
    public StackPane sitesContainer;
    @FXML
    public Button buildingsButton;
    @FXML
    public Text island_name;
    @FXML
    public Text island_inf;
    @FXML
    public Text crewCapacity;
    @FXML
    public Text resCapacity;
    @FXML
    public TextField inputIslandName;

    @SubComponent
    @Inject
    public SitesComponent sitesComponent;
    @SubComponent
    @Inject
    public DetailsComponent detailsComponent;
    @SubComponent
    @Inject
    public BuildingsComponent buildingsComponent;
    @Inject
    public InGameService inGameService;
    @Inject
    public IslandAttributeStorage islandAttributes;

    private InGameController inGameController;

    @Inject
    public OverviewSitesComponent() {

    }

    public void showDetails() {
        detailsButton.setDisable(true);
        sitesButton.setDisable(false);
        buildingsButton.setDisable(false);
        inGameService.showOnly(sitesContainer, detailsComponent);
    }

    public void showUpgrades() {
        if(islandAttributes.getIsland().upgradeLevel() == 4){
            inGameController.overviewUpgradeComponent.upgrade_box.setVisible(false);
            inGameController.overviewUpgradeComponent.upgrade_box.setMouseTransparent(true);
        } else {
            inGameController.overviewUpgradeComponent.upgrade_box.setVisible(true);
            inGameController.overviewUpgradeComponent.upgrade_box.setMouseTransparent(false);
        }
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewUpgradeComponent);
        inGameController.overviewUpgradeComponent.setUpgradeButton();
        inGameController.overviewUpgradeComponent.setNeededResources();
    }

    public void showBuildings() {
        buildingsButton.setDisable(true);
        sitesButton.setDisable(false);
        detailsButton.setDisable(false);
        buildingsComponent.setGridPane();
        inGameService.showOnly(sitesContainer, buildingsComponent);
    }

    public void showSites() {
        detailsButton.setDisable(false);
        sitesButton.setDisable(true);
        buildingsButton.setDisable(false);
        sitesComponent.setSitesBox(islandAttributes.getIsland());
        inGameService.showOnly(sitesContainer, sitesComponent);
    }

    public void setContainer() {
        sitesContainer.setVisible(false);
        sitesContainer.getChildren().add(sitesComponent);
        sitesContainer.getChildren().add(detailsComponent);
        sitesContainer.getChildren().add(buildingsComponent);
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void closeOverview() {
        resetButtons();
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.rudderImage.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;
        if (islandAttributes.getIsland().flagIndex() >= 0) {
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
        }
        inGameController.selectedIsland = null;
    }

    public void resetButtons(){
        detailsButton.setDisable(false);
        sitesButton.setDisable(false);
    }

    public void setOverviewSites() {
        int usedSlots = sitesComponent.getTotalSiteSlots(islandAttributes.getIsland()) +
                islandAttributes.getIsland().buildings().size();
        island_name.setText(String.valueOf(islandAttributes.getIsland().type()));
        crewCapacity.setText(String.valueOf(islandAttributes.getIsland().crewCapacity()));
        resCapacity.setText("Resources: " + usedSlots + "/" + islandAttributes.getIsland().resourceCapacity());
    }
}
