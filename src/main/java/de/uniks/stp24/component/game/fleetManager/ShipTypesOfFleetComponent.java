package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Ships;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;

import javax.inject.Inject;

@Component(view = "ShipTypesOfFleet.fxml")
public class ShipTypesOfFleetComponent extends VBox implements ReusableItemComponent<Ships.BlueprintInFleetDto> {
    @FXML
    public Label typeLabel;
    @FXML
    public Label sizeLabel;

    final FleetManagerComponent fleetManagerComponent;

    @Inject
    public ShipTypesOfFleetComponent(FleetManagerComponent fleetManagerComponent){
        this.fleetManagerComponent = fleetManagerComponent;
    }

    public void setItem(Ships.BlueprintInFleetDto blueprintInFleetDto){
        this.typeLabel.setText(blueprintInFleetDto.type());
        this.sizeLabel.setText(String.valueOf(blueprintInFleetDto.count()));
    }

    public void buildShip(){}

    public void decrementSize(){}

    public void incrementSize(){}
}
