package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.ShortSystemDto;
import de.uniks.stp24.model.EffectSource;
import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.model.Ships.BlueprintInFleetDto;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.model.Ships.ShipType;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.stream.Collectors;

@Component(view = "FleetManager.fxml")
public class FleetManagerComponent extends AnchorPane {
    @FXML
    public VBox fleetsOverviewVBox;
    @FXML
    public ListView<Fleet> fleetsListView;
    @FXML
    public ListView<BlueprintInFleetDto> blueprintInFleetListView;
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
    public ListView<ShipType> blueprintsListView;
    @FXML
    public VBox shipsVBox;
    @FXML
    public ListView<ReadShipDTO> shipsListView;
    @FXML
    public VBox selectIslandVBox;
    @FXML
    public Label islandNameLabel;
    @FXML
    public VBox infoButtonVBox;

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
    @Inject
    IslandsService islandsService;

    private int islandNameIndex = 0;
    private List<ShortSystemDto> islandList = new ArrayList<>();
    private Fleet editedFleet;

    public Provider<FleetComponent> fleetComponentProvider = () -> new FleetComponent(this, this.tokenStorage, this.subscriber, this.fleetService);// technologyService, app, technologiesResourceBundle, this.imageCache);
    public Provider<ShipTypesOfFleetComponent> shipTypesOfFleetComponentProvider = () -> new ShipTypesOfFleetComponent(this, this.resourcesService, this.shipService,this.subscriber, this.fleetService);// technologyService, app, technologiesResourceBundle, this.imageCache);
    public Provider<BlueprintsComponent> blueprintsAddableComponentProvider = () -> new BlueprintsComponent(this, true);
    public Provider<BlueprintsComponent> blueprintsNotAddableComponentProvider = () -> new BlueprintsComponent(this, false);
    public Provider<ShipComponent> shipComponentProvider = ()-> new ShipComponent(this, this.tokenStorage, this.subscriber, this.shipService);

    public Map<String, Integer> blueprintsInFleetMap = new HashMap<>();
    public ObservableList<BlueprintInFleetDto> blueprintsInFleetList = FXCollections.observableArrayList();
    public ObservableList<Fleet> fleets = FXCollections.observableArrayList();
    public ObservableList<ShipType> blueprints = FXCollections.observableArrayList();
    public ObservableList<ReadShipDTO> ships = FXCollections.observableArrayList();

    @Inject
    public FleetManagerComponent(){}

    @OnRender
    public void render() {
        this.fleets = this.fleetService.getEmpireFleets(this.tokenStorage.getEmpireId());
        this.fleetsListView.setItems(fleets);
        this.fleetsListView.setCellFactory(list -> new ComponentListCell<>(app, fleetComponentProvider));
        this.blueprintInFleetListView.setItems(blueprintsInFleetList);
        this.blueprintInFleetListView.setCellFactory(list -> new ComponentListCell<>(app,shipTypesOfFleetComponentProvider));
        FilteredList<ShipType> filteredBlueprints = new FilteredList<>(blueprints, shipType -> shipType.build_time() > 0);
        this.blueprintsListView.setItems(filteredBlueprints);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app,blueprintsNotAddableComponentProvider));
        this.shipsListView.setItems(ships);
        this.shipsListView.setCellFactory(list -> new ComponentListCell<>(app,shipComponentProvider));

//        this.infoButtonVBox.setVisible(false);
//        this.selectIslandVBox.setVisible(false);
//        this.blueprintsVBox.setVisible(true);
//        this.fleetsOverviewVBox.setVisible(true);
//        this.fleetBuilderVBox.setVisible(false);
//        this.shipsVBox.setVisible(false);
    }

    public void showFleets(){
        this.blueprintsInFleetMap.clear();
        this.blueprintsInFleetList.clear();
        this.ships.clear();
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app,blueprintsNotAddableComponentProvider));
        showBlueprints();
        removeEditedFleetInformation();
        this.infoButtonVBox.setVisible(false);
        this.selectIslandVBox.setVisible(false);
        this.fleetsListView.setVisible(true);
        this.fleetBuilderVBox.setVisible(false);
    }

    public void showBlueprints(){
        this.blueprints.clear();
        this.blueprints.addAll(shipService.shipTypesAttributes);
        this.shipsVBox.setVisible(false);
        this.blueprintsVBox.setVisible(true);
    }

    public void showShips(){
        this.shipsVBox.setVisible(true);
        this.blueprintsVBox.setVisible(false);
    }

    public void removeEditedFleetInformation(){
        this.shipService.removeShipListener();
        this.shipService.clearShipList();
        this.editedFleet = null;
    }

    public void close() {
        this.setVisible(false);
    }

    public void editSelectedFleet(Fleet fleet) {
        this.ships.clear();
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app,blueprintsAddableComponentProvider));
        this.editedFleet = fleet;
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()),
                dto -> {
                    Arrays.stream(dto).forEach(ship -> {
                        this.ships.add(ship);
                        if (!this.blueprintsInFleetMap.containsKey(ship.type())) {
                            this.blueprintsInFleetMap.put(ship.type(), 1);
                        } else {
                            this.blueprintsInFleetMap.compute(ship.type(), (k, currentCount) -> currentCount + 1);
                        }
                    });
                    //Todo: remove print
                    System.out.println(fleet.size() + "in editSelectedFleet");
                    this.shipService.setShipList(ships);
                    this.shipService.initializeShipListeners(fleet._id());
                    fleet.size().entrySet().forEach(entry ->
                            blueprintsInFleetMap.putIfAbsent(entry.getKey(), 0)
                    );
                    this.blueprintsInFleetList.clear();
                    this.blueprintsInFleetList.addAll(this.blueprintsInFleetMap.entrySet().stream().map(entry ->
                            new BlueprintInFleetDto(entry.getKey(), entry.getValue(), fleet)).toList());
                    showBlueprints();
                    this.infoButtonVBox.setVisible(true);
                    this.fleetNameText.setText(fleet.name());
                    this.fleetsListView.setVisible(false);
                    this.fleetBuilderVBox.setVisible(true);
                },
                error -> System.out.println("Error loading ships of a fleet in FleetManagerComponent:\n" + error.getMessage())
        );
    }

    public void addBlueprintToFleet(ShipType shipType) {
        if (!this.editedFleet.size().containsKey(shipType._id())) {
            this.blueprintsInFleetList.add(new BlueprintInFleetDto(shipType._id(), 0, this.editedFleet));
            this.subscriber.subscribe(this.fleetService.editSizeOfFleet(shipType._id(), 1, editedFleet),
                    dto -> {
                        this.blueprintInFleetListView.refresh();
                    },
                    error -> System.out.println("Error while adding a Blueprint to a FleetManagerComponent:\n" + error.getMessage()));
        }
    }

    public void showNextIslandName(){
        this.islandNameIndex = this.islandNameIndex + 1 < this.islandList.size() ? this.islandNameIndex + 1 : 0;
        setIslandNameText(this.islandNameIndex);
    }

    public void showLastIslandName(){
        this.islandNameIndex = this.islandNameIndex - 1 >= 0 ? this.islandNameIndex - 1 : this.islandList.size() - 1;
        setIslandNameText(this.islandNameIndex);
    }

    public void setIslandNameText(int index){
        if(islandList.get(index).buildings().contains("shipyard")) {
            this.islandNameLabel.setText(islandList.get(index).name() + " (has shipyard)");
        }else{
            this.islandNameLabel.setText(islandList.get(index).name());
        }
    }

    public void createFleet() {
        this.selectIslandVBox.setVisible(true);
        this.islandList.addAll(islandsService.getDevIsles());
        this.islandList = islandList.stream()
                .filter(shortSystemDto -> shortSystemDto.owner().equals(tokenStorage.getEmpireId()))
                .collect(Collectors.toList());
        this.islandNameIndex = 0;
        setIslandNameText(0);
    }

    public void confirmIsland() {
        //Todo: random fleetName
        Fleets.CreateFleetDTO newFleet = new Fleets.CreateFleetDTO("newFleet",
                this.islandList.get(islandNameIndex)._id(), new HashMap<>(),
                new HashMap<>(), new HashMap<>(), new EffectSource[]{});
        this.subscriber.subscribe(this.fleetService.createFleet(this.tokenStorage.getGameId(), newFleet),
                result -> {
                    this.selectIslandVBox.setVisible(false);
                    showFleets();
                },
                error -> System.out.println("Error while creating a new fleet in the FleetManagerComponent:\n" + error.getMessage())
        );
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }



}
