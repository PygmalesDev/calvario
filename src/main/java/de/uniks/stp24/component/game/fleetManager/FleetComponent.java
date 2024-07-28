package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "Fleet.fxml")
public class FleetComponent extends VBox implements ReusableItemComponent<Fleet> {
    @FXML
    public Label fleetNameLabel;
    @FXML
    public ImageView fleetImageview;
    @FXML
    public Label sizeLabel;


    final TokenStorage tokenStorage;
    final Subscriber subscriber;
    final FleetService fleetService;
    final FleetManagerComponent fleetManagerComponent;
    //final Subscriber subscriber;
    Fleet fleet;

    @Inject
    public FleetComponent(FleetManagerComponent fleetManagerComponent, TokenStorage tokenStorage, Subscriber subscriber, FleetService fleetService){
        this.fleetManagerComponent = fleetManagerComponent;
        this.subscriber = subscriber;
        this.tokenStorage = tokenStorage;
        this.fleetService = fleetService;
    }



    public void setItem(Fleet fleet){
        fleetNameLabel.setText(fleet.name());
        this.fleet = fleet;
    }


    public void deleteFleet(){
        this.subscriber.subscribe(this.fleetService.deleteFleet(this.tokenStorage.getGameId(), this.fleet._id()),
                result -> {},
                error -> System.out.println("Error while deleting a fleet in the FleetComponent:\n" + error.getMessage()));
    }

    public void editFleet(){
        this.fleetManagerComponent.editSelectedFleet(fleet);
    }
}
