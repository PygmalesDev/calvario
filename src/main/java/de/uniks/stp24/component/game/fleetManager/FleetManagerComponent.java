package de.uniks.stp24.component.game.fleetManager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

@Component(view = "FleetManager.fxml")
public class FleetManagerComponent extends AnchorPane {
    @FXML
    public VBox fleetsOverviewVBox;
    @FXML
    public ListView fleetsListView;
    @FXML
    public VBox fleetBuilderVBox;
    @FXML
    public Text fleetNameText;
    @FXML
    public ImageView shipImageView;
    @FXML
    public Label commandLimitLabel;
    @FXML
    public Label islandLabel;
    @FXML
    public Button shipsButton;
    @FXML
    public Button blueprintButton;
    @FXML
    public VBox blueprintsVBox;
    @FXML
    public ListView blueprintsListView;
    @FXML
    public VBox shipsVBox;
    @FXML
    public ListView shipsListView;


    public void showFleets(){}

    public void showBlueprints(){}

    public void showShips(){}

    public void close() {
        this.setVisible(false);
    }
}
