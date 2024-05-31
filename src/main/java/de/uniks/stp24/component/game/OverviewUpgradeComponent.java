package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.BasicController;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {

    @Inject
    public OverviewUpgradeComponent() {

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
}
