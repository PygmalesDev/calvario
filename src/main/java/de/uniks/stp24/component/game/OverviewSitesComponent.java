package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.InGameService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
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
    public IslandComponent islandComponent;

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
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewUpgradeComponent);
    }

    public void showBuildings(){
        buildingsButton.setDisable(true);
        sitesButton.setDisable(false);
        detailsButton.setDisable(false);
        inGameService.showOnly(sitesContainer, buildingsComponent);
    }

    public void showSites(){
        detailsButton.setDisable(false);
        sitesButton.setDisable(true);
        buildingsButton.setDisable(false);
        inGameService.showOnly(sitesContainer, sitesComponent);
    }

    public void setContainer() {
        sitesContainer.setVisible(false);
        sitesContainer.getChildren().add(sitesComponent);
        sitesContainer.getChildren().add(detailsComponent);
    }

    public void setIngameController(InGameController inGameController){
        this.inGameController = inGameController;
    }

    public void closeOverview(){
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.rudderImage.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;
        inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
        inGameController.selectedIsland = null;
    }

}
