package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.ShortSystemDto;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Ships.BlueprintInFleetDto;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Ships.ShipType;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import java.util.ResourceBundle;

import java.util.List;

import static de.uniks.stp24.model.Jobs.Job;

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
    public Label fleetNameText;
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
    public VBox infoButtonVBox;
    @FXML
    public StackPane fleetManagerStackPane;
    @FXML
    public Button closeFleetManagerButton;
    @FXML
    public Button createFleetButton;
    @FXML
    public Button showFleetsButton;
    @FXML
    public Label buildShipErrorLabel;


    @Inject
    App app;
    @Inject
    public FleetService fleetService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public JobsService jobsService;
    @Inject
    public VariableService variableService;
    @Inject
    public ShipService shipService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public ResourcesService resourcesService;
    @Inject
    public IslandsService islandsService;

    @SubComponent
    @Inject
    public NewFleetComponent newFleetComponent;
    @SubComponent
    @Inject
    public ChangeFleetComponent changeFleetComponent;
    @SubComponent
    @Inject
    public BlueprintsDetailsComponent blueprintsDetailsComponent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    ImageCache imageCache = new ImageCache();

    private Fleet editedFleet;
    private FadeTransition transition;

    public Provider<FleetComponent> fleetComponentProvider = () -> new FleetComponent(this, this.tokenStorage, this.subscriber, this.fleetService);
    public Provider<ShipTypesOfFleetComponent> shipTypesOfFleetComponentProvider = () -> new ShipTypesOfFleetComponent(this, this.resourcesService, this.shipService, this.subscriber, this.fleetService);
    public Provider<BlueprintsComponent> blueprintsAddableComponentProvider = () -> new BlueprintsComponent(this, true, this.blueprintsDetailsComponent, gameResourceBundle);
    public Provider<BlueprintsComponent> blueprintsNotAddableComponentProvider = () -> new BlueprintsComponent(this, false, this.blueprintsDetailsComponent, gameResourceBundle);
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
        this.newFleetComponent.setFleetManager(this);
        this.changeFleetComponent.setFleetManager(this);
    }

    @OnRender
    public void render() {

        blueprintInFleetListView.setSelectionModel(null);
        fleetsListView.setSelectionModel(null);
        blueprintsListView.setSelectionModel(null);
        shipsListView.setSelectionModel(null);

        this.fleets = this.fleetService.getEmpireFleets(this.tokenStorage.getEmpireId());
        this.ships = this.shipService.getShipsInSelectedFleet();
        this.blueprintsInFleetList = this.shipService.getBlueprintsInSelectedFleet();
        FilteredList<ShipType> filteredBlueprints = new FilteredList<>(blueprints, shipType -> shipType.build_time() > 0);

        this.fleetsListView.setItems(fleets);
        this.fleetsListView.setCellFactory(list -> new ComponentListCell<>(app, fleetComponentProvider));
        this.blueprintInFleetListView.setItems(blueprintsInFleetList);
        this.blueprintInFleetListView.setCellFactory(list -> new ComponentListCell<>(app, shipTypesOfFleetComponentProvider));
        this.blueprintsListView.setItems(filteredBlueprints);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));
        this.shipsListView.setItems(ships);
        this.shipsListView.setCellFactory(list -> new ComponentListCell<>(app, shipComponentProvider));

        this.fleetManagerStackPane.getChildren().add(this.newFleetComponent);
        this.fleetManagerStackPane.getChildren().add(this.changeFleetComponent);
        this.newFleetComponent.setVisible(false);
        this.changeFleetComponent.setVisible(false);

        this.transition = new FadeTransition(Duration.seconds(5), this.buildShipErrorLabel);
    }

    public void showFleets() {
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));
        showBlueprints();
        this.fleetsListView.refresh();
        this.shipService.clearEditedFleetInfos();
        shipImageView.setImage(imageCache.get("icons/ships/ship_Image_With_Frame1.png"));

        commandLimitLabel.setVisible(false);
        islandLabel.setVisible(false);
        blueprintButton.setVisible(false);
        shipsButton.setVisible(false);
        createFleetButton.setVisible(true);

        this.fleetNameText.setText("Fleets");

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
        this.buildShipErrorLabel.setVisible(false);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsAddableComponentProvider));
        this.editedFleet = fleet;
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()),
                dto -> {
                    createFleetButton.setVisible(false);

                    this.shipService.initializeFleetEdition(dto, editedFleet);
                    showBlueprints();
                    setIslandName(false);
                    setCommandLimit(fleet,false);
                    shipImageView.setImage(imageCache.get("icons/ships/ship_Image_With_Frame2.png"));

                    commandLimitLabel.setVisible(true);
                    islandLabel.setVisible(true);
                    blueprintButton.setVisible(true);
                    shipsButton.setVisible(true);

                    this.fleetNameText.setText(fleet.name());
                    this.fleetsListView.setVisible(false);
                    this.fleetBuilderVBox.setVisible(true);
                }, error -> System.out.println("Error loading ships of a fleet in FleetManagerComponent:\n" + error.getMessage())
        );
    }

    public void addBlueprintToFleet(ShipType shipType) {
        if ((!this.editedFleet.size().containsKey(shipType._id())) || (this.editedFleet.size().get(shipType._id()) == 0)){
            this.subscriber.subscribe(this.fleetService.editSizeOfFleet(shipType._id(), 1, editedFleet),
                    dto -> {
                        this.shipService.addBlueprintToFleet(new BlueprintInFleetDto(shipType._id(), 0, this.editedFleet));
                        this.setCommandLimit(dto,false);
                    }, error -> System.out.println("Error while adding a Blueprint to a FleetManagerComponent:\n" + error.getMessage()));
        }
    }

    public void setCommandLimit(Fleet fleet, boolean shipDeleted) {
        int numberOfShips = ships.size();
        System.out.println(fleet.ships() + " fleet ships");
        System.out.println(editedFleet.ships() + " edited fleet ships");
        if(shipDeleted && ships.size() == fleet.ships()) {
            numberOfShips = ships.size() - 1;
        }
        this.commandLimitLabel.setText("Command Limit \n" + numberOfShips + " / "
                + fleet.size().values().stream().mapToInt(Integer::intValue).sum());
    }

    public void setIslandName(boolean shipJobStarted) {
        List<ShortSystemDto> islands = islandsService.getDevIsles().stream().filter(island -> island._id().equals(this.editedFleet.location())).toList();
        if(islands.isEmpty()){
            this.islandLabel.setText("Unknown Seas");
        } else if (!islands.getFirst().owner().equals(this.tokenStorage.getEmpireId())) {
            this.islandLabel.setText(islands.getFirst().name() + "\nNot your island!");
        } else {
            int numberOfShipyards = islands.getFirst().buildings().stream().filter("shipyard"::equals).toList().size();
            int numberOfShipJobs = this.jobsService.getObservableListForSystem(this.editedFleet.location())
                    .filtered(job -> job.type().equals("ship")).size();
            if(shipJobStarted){
                numberOfShipJobs += 1;
            }
            this.islandLabel.setText(islands.getFirst().name() + "\n" + numberOfShipJobs + " / " + numberOfShipyards + " shipyards occupied");
        }
    }

    public void setShipFinisher(Job job){
        this.jobsService.onJobCompletion(job._id(), ()  -> {
            this.blueprintInFleetListView.refresh();
            setCommandLimit(this.fleetService.getFleet(editedFleet._id()), false);
            setIslandName(false);
        });
        this.jobsService.onJobDeletion(job._id(), ()  -> setIslandName(false));
    }

    public void createFleet() {
        this.newFleetComponent.createNewFleet();
    }

    public void setErrorLabel(String error){
        this.transition.stop();
        this.buildShipErrorLabel.setVisible(true);
        this.transition.setFromValue(1);
        this.transition.setToValue(0);
        this.transition.play();
        switch (error) {
            case "resources" -> this.buildShipErrorLabel.setText("You don't have enough resources!");
            case "shipyard" -> this.buildShipErrorLabel.setText("All your shipyards are occupied!");
            case "plannedSize" -> this.buildShipErrorLabel.setText("You have already built all planned ships!");
            case "successful" -> this.buildShipErrorLabel.setText("The construction of your new ship has started!");
            case "wilderness", "enemiesIsland" -> this.buildShipErrorLabel.setText("You don't own this island!");
        }
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }

}
