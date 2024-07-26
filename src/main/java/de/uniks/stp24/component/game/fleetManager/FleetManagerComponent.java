package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.technology.TechnologyCategorySubComponent;
import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.model.Ships.ShipType;
import de.uniks.stp24.rest.ShipsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.ShipService;
import de.uniks.stp24.service.game.VariableService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow;

@Component(view = "FleetManager.fxml")
public class FleetManagerComponent extends AnchorPane {
    @FXML
    public VBox fleetsOverviewVBox;
    @FXML
    public ListView<Fleet> fleetsListView;
    @FXML
    public ListView<Ships.BlueprintInFleetDto> blueprintInFleetListView;
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
    public ListView<Ship> shipsListView;

    @Inject
    App app;
    @Inject
    FleetService fleetService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    VariableService variableService;
    @Inject
    ShipService shipService;
    @Inject
    Subscriber subscriber;
    @Inject
    ResourcesService resourcesService;

    Map<String, Integer> blueprintsInFleetMap = new HashMap<>();
    ObservableList<Fleet> fleets = FXCollections.observableArrayList();;
    public Provider<FleetComponent> fleetComponentProvider = () -> new FleetComponent(this);// technologyService, app, technologiesResourceBundle, this.imageCache);
    ObservableList<Ships.BlueprintInFleetDto> blueprintsInFleetList = FXCollections.observableArrayList();
    public Provider<ShipTypesOfFleetComponent> shipTypesOfFleetComponentProvider = () -> new ShipTypesOfFleetComponent(this, this.resourcesService, this.shipService,this.subscriber);// technologyService, app, technologiesResourceBundle, this.imageCache);



    @Inject
    public FleetManagerComponent(){}

    @OnInit
    public void init(){

    }

    @OnRender
    public void render() {
        fleetBuilderVBox.setVisible(false);
        shipsVBox.setVisible(false);


        this.fleets = this.fleetService.getEmpireFleets(this.tokenStorage.getEmpireId());
        this.fleetsListView.setItems(fleets);
        this.fleetsListView.setCellFactory(list -> new ComponentListCell<>(app, fleetComponentProvider));
        this.blueprintInFleetListView.setItems(blueprintsInFleetList);
        this.blueprintInFleetListView.setCellFactory(list -> new ComponentListCell<>(app,shipTypesOfFleetComponentProvider));

    }

    public void showFleets(){
        this.blueprintsInFleetList.clear();
        fleetsListView.setVisible(true);
        fleetBuilderVBox.setVisible(false);
    }

    public void showBlueprints(){}

    public void showShips(){}

    public void close() {
        this.setVisible(false);
    }

    public void editSelectedFleet(Fleet fleet) {
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()),
                dto -> {Arrays.stream(dto).forEach(ship -> {
                    if(!this.blueprintsInFleetMap.containsKey(ship.type())) {
                        this.blueprintsInFleetMap.put(ship.type(), 1);
                    } else {
                        this.blueprintsInFleetMap.compute(ship.type(), (k, currentCount) -> currentCount + 1);
                    }
                });
            this.blueprintsInFleetList.clear();
            this.blueprintsInFleetList.addAll(this.blueprintsInFleetMap.entrySet().stream().map(entry ->
                    new Ships.BlueprintInFleetDto(entry.getKey(), entry.getValue(), fleet)).toList());
            },
                error -> System.out.println("Error loading ships of a fleet in FleetManagerComponent:\n" + error.getMessage()));


        fleetsListView.setVisible(false);
        fleetBuilderVBox.setVisible(true);
    }
}
