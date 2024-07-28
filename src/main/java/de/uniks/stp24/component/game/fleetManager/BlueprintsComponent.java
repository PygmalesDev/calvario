package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.model.Ships.ShipType;
import javafx.fxml.FXML;
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

    @Inject
    public BlueprintsComponent() {}

    @Override
    public void setItem(@NotNull ShipType shipType) {
        blueprintTypeLabel.setText(shipType._id());
        healthLabel.setText(String.valueOf(shipType.health()));
        timeLabel.setText(String.valueOf(shipType.build_time()));
        speedLabel.setText(String.valueOf(shipType.speed()));
        attackLabel.setText(String.valueOf(shipType.attack().get("default")));
        defenseLabel.setText(String.valueOf(shipType.defense().get("default")));
    }

    public void addBlueprint(){}
}
