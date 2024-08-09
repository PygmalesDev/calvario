package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Ships.ShipType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Blueprints.fxml")
public class BlueprintsComponent extends VBox implements ReusableItemComponent<ShipType> {
    @FXML
    public Label blueprintTypeLabel;
    @FXML
    public ImageView healthImage;
    @FXML
    public Label healthLabel;
    @FXML
    public ImageView timeImage;
    @FXML
    public Label timeLabel;
    @FXML
    public ImageView speedImage;
    @FXML
    public Label speedLabel;
    @FXML
    public HBox blueprintHBox;
    @FXML
    public ImageView attackImage;
    @FXML
    public Label attackLabel;
    @FXML
    public ImageView defenseImage;
    @FXML
    public Label defenseLabel;
    @FXML
    public Button addBlueprintButton;

    private final FleetManagerComponent fleetManagerComponent;
    private final boolean canBeAddedToFleet;
    private ShipType shipType;

    @Inject
    public BlueprintsComponent(FleetManagerComponent fleetManagerComponent, boolean canBeAddedToFleet) {
        this.fleetManagerComponent = fleetManagerComponent;
        this.canBeAddedToFleet = canBeAddedToFleet;
    }

    @Override
    public void setItem(@NotNull ShipType shipType) {
        this.addBlueprintButton.setId("addBlueprintButton_" + shipType._id());

        this.shipType = shipType;
        this.blueprintTypeLabel.setText(shipType._id());
        this.healthLabel.setText(String.valueOf(shipType.health()));
        this.timeLabel.setText(String.valueOf(shipType.build_time()));
        this.speedLabel.setText(String.valueOf(shipType.speed()));
        this.attackLabel.setText(String.valueOf(shipType.attack().get("default")));
        this.defenseLabel.setText(String.valueOf(shipType.defense().get("default")));
        this.addBlueprintButton.setVisible(canBeAddedToFleet);
    }

    public void addBlueprint(){
        this.fleetManagerComponent.addBlueprintToFleet(shipType);
    }
}
