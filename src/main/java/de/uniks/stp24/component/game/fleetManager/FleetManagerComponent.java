package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.ShortSystemDto;
import de.uniks.stp24.model.EffectSource;
import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Ships.BlueprintInFleetDto;
import de.uniks.stp24.model.Ships.ReadShipDTO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @FXML
    public Label newFleetOfShipNameLabel;
    @FXML
    public VBox selectNewFleetVBox;

    @Inject
    App app;
    @Inject
    FleetService fleetService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    JobsService jobsService;
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

    private int fleetNameIndex = 0;
    private int islandNameIndex = 0;
    private List<ShortSystemDto> islandList = new ArrayList<>();
    private List<Fleet> fleetsOnIslandList = new ArrayList<>();
    private Fleet editedFleet;
    private ReadShipDTO readShipDTOFleetChange;

    public Provider<FleetComponent> fleetComponentProvider = () -> new FleetComponent(this, this.tokenStorage, this.subscriber, this.fleetService);
    public Provider<ShipTypesOfFleetComponent> shipTypesOfFleetComponentProvider = () -> new ShipTypesOfFleetComponent(this, this.resourcesService, this.shipService, this.subscriber, this.fleetService);
    public Provider<BlueprintsComponent> blueprintsAddableComponentProvider = () -> new BlueprintsComponent(this, true);
    public Provider<BlueprintsComponent> blueprintsNotAddableComponentProvider = () -> new BlueprintsComponent(this, false);
    public Provider<ShipComponent> shipComponentProvider = () -> new ShipComponent(this, this.subscriber, this.shipService, this.fleetService);

    public ObservableList<BlueprintInFleetDto> blueprintsInFleetList = FXCollections.observableArrayList();
    public ObservableList<Fleet> fleets = FXCollections.observableArrayList();
    public ObservableList<ShipType> blueprints = FXCollections.observableArrayList();
    public ObservableList<ReadShipDTO> ships = FXCollections.observableArrayList();

    @Inject
    public FleetManagerComponent() {
    }

    @OnInit
    public void init(){
        this.jobsService.onJobsLoadingFinished("ship", this::setShipFinisher);
    }

    @OnRender
    public void render() {
        this.fleets = this.fleetService.getEmpireFleets(this.tokenStorage.getEmpireId());
        this.ships = this.shipService.getShipsInSelectedFleet();
        this.blueprintsInFleetList = this.shipService.getBlueprintsInSelectedFleet();
        this.fleetsListView.setItems(fleets);
        this.fleetsListView.setCellFactory(list -> new ComponentListCell<>(app, fleetComponentProvider));
        this.blueprintInFleetListView.setItems(blueprintsInFleetList);
        this.blueprintInFleetListView.setCellFactory(list -> new ComponentListCell<>(app, shipTypesOfFleetComponentProvider));
        FilteredList<ShipType> filteredBlueprints = new FilteredList<>(blueprints, shipType -> shipType.build_time() > 0);
        this.blueprintsListView.setItems(filteredBlueprints);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));
        this.shipsListView.setItems(ships);
        this.shipsListView.setCellFactory(list -> new ComponentListCell<>(app, shipComponentProvider));
    }

    public void showFleets() {
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));
        showBlueprints();
        this.fleetsListView.refresh();
        this.shipService.clearEditedFleetInfos();
        this.infoButtonVBox.setVisible(false);
        this.selectIslandVBox.setVisible(false);
        this.selectNewFleetVBox.setVisible(false);
        this.fleetsListView.setVisible(true);
        this.fleetBuilderVBox.setVisible(false);
    }

    public void showBlueprints() {
        this.blueprints.clear();
        this.blueprints.addAll(shipService.shipTypesAttributes);
        this.shipsVBox.setVisible(false);
        this.blueprintsVBox.setVisible(true);
    }

    public void showShips() {
        this.shipsVBox.setVisible(true);
        this.blueprintsVBox.setVisible(false);
    }


    public void close() {
        this.setVisible(false);
    }

    public void editSelectedFleet(Fleet fleet) {
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsAddableComponentProvider));
        this.editedFleet = fleet;
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()),
                dto -> {
                    this.shipService.initializeFleetEdition(dto, editedFleet);
                    showBlueprints();
                    setIslandName(fleet);
                    setCommandLimit(fleet,false);
                    this.infoButtonVBox.setVisible(true);
                    this.fleetNameText.setText(fleet.name());
                    this.fleetsListView.setVisible(false);
                    this.fleetBuilderVBox.setVisible(true);
                },
                error -> System.out.println("Error loading ships of a fleet in FleetManagerComponent:\n" + error.getMessage())
        );
    }

    public void addBlueprintToFleet(ShipType shipType) {
        if ((!this.editedFleet.size().containsKey(shipType._id())) || (this.editedFleet.size().get(shipType._id()) == 0)){ // && this.shipService.checkNumberOfShipsOfTypeInFleet(shipType._id()))) {
            this.subscriber.subscribe(this.fleetService.editSizeOfFleet(shipType._id(), 1, editedFleet),
                    dto -> {
                        this.shipService.addBlueprintToFleet(new BlueprintInFleetDto(shipType._id(), 0, this.editedFleet));
                        this.setCommandLimit(dto,false);
                    },
                    error -> System.out.println("Error while adding a Blueprint to a FleetManagerComponent:\n" + error.getMessage()));
        }
    }

    public void showNextIslandName() {
        this.islandNameIndex = this.islandNameIndex + 1 < this.islandList.size() ? this.islandNameIndex + 1 : 0;
        setIslandNameText(this.islandNameIndex);
    }

    public void showLastIslandName() {
        this.islandNameIndex = this.islandNameIndex - 1 >= 0 ? this.islandNameIndex - 1 : this.islandList.size() - 1;
        setIslandNameText(this.islandNameIndex);
    }

    public void setIslandNameText(int index) {
        int numberOfShipyards = (int) this.islandList.get(index).buildings().stream()
                .filter("shipyard"::equals)
                .count();
        this.islandNameLabel.setText(islandList.get(index).name() + " (has " + numberOfShipyards + " shipyard)");
    }

    public void createFleet() {
        this.selectIslandVBox.setVisible(true);
        this.islandList.addAll(islandsService.getDevIsles());
        this.islandList = islandList.stream()
                .filter(shortSystemDto -> shortSystemDto.owner().equals(tokenStorage.getEmpireId()) && shortSystemDto.buildings().contains("shipyard"))
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

    public void setCommandLimit(Fleet fleet, boolean shipDeleted) {
        int numberOfShips = ships.size();
        System.out.println(fleet.ships() + " fleet ships");
        System.out.println(editedFleet.ships() + " edited fleet ships");
        if(shipDeleted && ships.size() == fleet.ships()) {
            numberOfShips = ships.size() - 1;
        }
        this.commandLimitLabel.setText("Command Limit \n" + numberOfShips + " / " + fleet.size().values().stream().mapToInt(Integer::intValue).sum());
    }

    public void setIslandName(Fleet fleet) {
        List<ShortSystemDto> systemList = islandsService.getDevIsles().stream().filter(shortSystemDto -> shortSystemDto._id().equals(fleet.location()))
                .toList();
        if (systemList.isEmpty()) {
            this.islandLabel.setText("Unknown Seas");
        } else {
            this.islandLabel.setText(systemList.getFirst().name());
        }
    }

    public void showLastFleetName() {
        this.fleetNameIndex = this.fleetNameIndex - 1 >= 0 ? this.fleetNameIndex - 1 : this.fleetsOnIslandList.size() - 1;
        this.newFleetOfShipNameLabel.setText(fleetsOnIslandList.get(fleetNameIndex).name());
    }

    public void showNextFleetName() {
        this.fleetNameIndex = this.fleetNameIndex + 1 < this.fleetsOnIslandList.size() ? this.fleetNameIndex + 1 : 0;
        this.newFleetOfShipNameLabel.setText(fleetsOnIslandList.get(fleetNameIndex).name());
    }

    public void confirmFleetChange() {
        subscriber.subscribe(this.shipService.changeFleetOfShip(fleetsOnIslandList.get(fleetNameIndex)._id(), readShipDTOFleetChange),
                ship -> {
                    this.shipService.deleteShipFromGroups(this.readShipDTOFleetChange);
                    this.fleetService.adaptShipCount(this.readShipDTOFleetChange.fleet(), -1);
                    this.fleetService.adaptShipCount(ship.fleet(),1);
                    this.selectNewFleetVBox.setVisible(false);
                },
                error -> System.out.println("Error while changing the fleet of a ship in the FleetManagerComponent:\n" + error.getMessage()));
    }

    public void changeFleetOfShip(ReadShipDTO readShipDTO) {
        this.fleetsOnIslandList = this.fleetService.getFleetsOnIsland(this.editedFleet.location()).stream()
                .filter(fleetDto -> !fleetDto._id().equals(editedFleet._id()) && fleetDto.empire().equals(editedFleet.empire()))
                .collect(Collectors.toList());
        this.fleetNameIndex = 0;
        this.readShipDTOFleetChange = readShipDTO;
        this.newFleetOfShipNameLabel.setText(fleetsOnIslandList.getFirst().name());
        this.selectNewFleetVBox.setVisible(true);
    }

    public void setShipFinisher(Jobs.Job job){
        this.jobsService.onJobCompletion(job._id(), event -> {
            this.blueprintInFleetListView.refresh();
            setCommandLimit(this.fleetService.getFleet(job.fleet()), false);
            System.out.println("ship job was finished and everything should be updated");
        });
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }

}
