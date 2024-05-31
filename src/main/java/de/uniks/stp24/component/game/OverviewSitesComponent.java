package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
@Component(view = "IslandOverviewSites.fxml")
public class OverviewSitesComponent extends AnchorPane {

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


    public void showDetails(ActionEvent actionEvent) {
    }

    public void showUpgrades(ActionEvent actionEvent) {
    }
}
