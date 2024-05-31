package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
    public OverviewSitesComponent() {

    }

    @OnInit
    void init() {

    }

    @OnRender
    void render() {

    }

    @OnDestroy
    void destroy() {

    }


    public void showDetails() {
    }

    public void showUpgrades() {
    }

    public void showSites(){
    }
}
