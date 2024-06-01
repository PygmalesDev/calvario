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
    public Button upgradesButton;
    @FXML
    public StackPane sitesContainer;

    @SubComponent
    @Inject
    public SitesComponent sitesComponent;
    @SubComponent
    @Inject
    public DetailsComponent detailsComponent;

    @Inject
    public InGameService inGameService;

    private InGameController inGameController;

    @Inject
    public OverviewSitesComponent() {

    }

    public void showDetails() {
        detailsButton.setDisable(true);
        sitesButton.setDisable(false);
        upgradesButton.setDisable(false);
        inGameService.showOnly(sitesContainer, detailsComponent);
    }

    public void showUpgrades() {
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewUpgradeComponent);
    }

    public void showSites(){
        detailsButton.setDisable(false);
        sitesButton.setDisable(true);
        upgradesButton.setDisable(false);
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

}
