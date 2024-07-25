package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.component.game.technology.TechnologyCategorySubComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Provider;

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

    public Provider<FleetComponent> provider = () -> new FleetComponent();//this, technologyService, app, technologiesResourceBundle, this.imageCache);

    @Inject
    public FleetManagerComponent(){}

    @OnRender
    public void render() {}

    public void showFleets(){}

    public void showBlueprints(){}

    public void showShips(){}

    public void close() {
        this.setVisible(false);
    }
}
