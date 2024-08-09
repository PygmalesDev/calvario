package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.ShipService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

import static de.uniks.stp24.model.Ships.ReadShipDTO;
import static de.uniks.stp24.model.Ships.ShipType;

@Component(view = "Ship.fxml")
public class ShipComponent extends VBox implements ReusableItemComponent<ReadShipDTO> {
    @FXML
    public Label blueprintTypeLabel;
    @FXML
    public ImageView healthImage;
    @FXML
    public Label healthLabel;
    @FXML
    public ImageView speedImage;
    @FXML
    public Label speedLabel;
    @FXML
    public ImageView attackImage;
    @FXML
    public Label attackLabel;
    @FXML
    public ImageView defenseImage;
    @FXML
    public Label defenseLabel;
    @FXML
    public Button deleteShipButton;
    @FXML
    public Button changeFleetButton;

    private final FleetManagerComponent fleetManagerComponent;
    private final ShipService shipService;
    private final FleetService fleetService;
    private final Subscriber subscriber;
    private ReadShipDTO readShipDTO;


    @Inject
    public ShipComponent(FleetManagerComponent fleetManagerComponent, Subscriber subscriber, ShipService shipService, FleetService fleetService){
        this.fleetManagerComponent = fleetManagerComponent;
        this.subscriber = subscriber;
        this.shipService = shipService;
        this.fleetService = fleetService;
    }


    @Override
    public void setItem(ReadShipDTO shipDTO){
        this.readShipDTO = shipDTO;
        this.blueprintTypeLabel.setText(shipDTO.type());
        this.healthLabel.setText(String.valueOf(shipDTO.health()));
        ShipType currentShipType = null;
        for(ShipType shipType : this.shipService.shipTypesAttributes){
            if (shipType._id().equals(shipDTO.type())) {
                currentShipType = shipType;
            }
        }
        assert currentShipType != null;
        this.speedLabel.setText(String.valueOf(currentShipType.speed()));
        this.attackLabel.setText(String.valueOf(currentShipType.attack().get("default")));
        this.defenseLabel.setText(String.valueOf(currentShipType.defense().get("default")));
    }

    public void changeFleet(){
        this.fleetManagerComponent.changeFleetComponent.changeFleetOfShip(this.readShipDTO);
    }

    public void deleteShip(){
        this.subscriber.subscribe(this.shipService.deleteShip(this.readShipDTO),
                result -> {
                    this.fleetManagerComponent.blueprintInFleetListView.refresh();
                    this.fleetManagerComponent.setCommandLimit(this.fleetService.getFleet(result.fleet()), true);
                }, error -> System.out.println("Error while deleting a ship in the ShipComponent:\n" + error.getMessage()));
    }
}
