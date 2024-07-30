package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Ships;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ShipService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

import static de.uniks.stp24.model.Ships.*;

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

    @Inject
    ShipService shipService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    private ReadShipDTO readShipDTO;


    @Inject
    public ShipComponent(){}

    @Override
    public void setItem(ReadShipDTO shipDTO){
        this.readShipDTO = shipDTO;
        blueprintTypeLabel.setText(shipDTO.type());
        healthLabel.setText(String.valueOf(shipDTO.health()));
        int shipTypeIndex = shipService.shipTypesAttributes.indexOf(shipDTO.type());
        ShipType shipType = shipService.shipTypesAttributes.get(shipTypeIndex);
        speedLabel.setText(String.valueOf(shipType.speed()));
        attackLabel.setText(String.valueOf(shipType.attack().get("default")));
        defenseLabel.setText(String.valueOf(shipType.defense().get("default")));
    }

    public void changeFleet(){}

    public void deleteShip(){
        this.subscriber.subscribe(this.shipService.deleteShip(readShipDTO),
                result -> {},
                error -> System.out.println("Error while deleting a ship in the ShipComponent:\n" + error.getMessage()));
    }
}
