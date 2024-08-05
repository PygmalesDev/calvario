package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Ships;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Component(view = "BlueprintsDetails.fxml")
public class BlueprintsDetailsComponent extends VBox {
    @FXML
    public Label shipType;

    @Inject
    public BlueprintsDetailsComponent() {

    }

    @OnRender
    public void render() {

    }

    @OnInit
    public void init() {

    }

    @OnDestroy
    public void destroy() {

    }

    public void showBlueprintDetails(Ships.ShipType shipType) {
        this.shipType.setText(shipType._id());
    }
}
