package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Fleets.Fleet;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;

import javax.inject.Inject;

@Component(view = "Fleet.fxml")
public class FleetComponent extends VBox implements ReusableItemComponent<Fleet> {
    @FXML
    public Label fleetNameLabel;
    @FXML
    public ImageView fleetImageview;
    @FXML
    public Label sizeLabel;

    final FleetManagerComponent fleetManagerComponent;
    Fleet fleet;

    @Inject
    public FleetComponent(FleetManagerComponent fleetManagerComponent){
        this.fleetManagerComponent = fleetManagerComponent;
    }



    public void setItem(Fleet fleet){
        fleetNameLabel.setText(fleet.name());
        this.fleet = fleet;
    }


    public void deleteFleet(){}

    public void editFleet(){
        this.fleetManagerComponent.editSelectedFleet(fleet);
    }
}
