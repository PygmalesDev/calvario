package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.InGameService;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {
    @Inject
    InGameService inGameService;

    private InGameController inGameController;

    @Inject
    public OverviewUpgradeComponent() {

    }

    public void goBack(){
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewSitesComponent);
    }

    public void closeOverview(){
        inGameController.islandClicked = false;
        inGameController.rudder_pain.setVisible(false);
        inGameController.overviewContainer.setVisible(false);
    }

    public void setIngameController(InGameController inGameController){
        this.inGameController = inGameController;
    }
}
