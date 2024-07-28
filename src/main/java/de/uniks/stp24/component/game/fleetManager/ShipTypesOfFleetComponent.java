package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.component.game.OverviewUpgradeComponent;
import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.ShipService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Objects;

@Component(view = "ShipTypesOfFleet.fxml")
public class ShipTypesOfFleetComponent extends VBox implements ReusableItemComponent<Ships.BlueprintInFleetDto> {
    @FXML
    public Label typeLabel;
    @FXML
    public Label sizeLabel;
    @FXML
    public Button buildShipButton;
    public final ResourcesService resourcesService;
    public final ShipService shipService;
    public final Subscriber subscriber;
    @Inject
    JobsService jobsService;

    final FleetManagerComponent fleetManagerComponent;
    private String shipType;
    private Fleets.Fleet fleet;
    private enum BUTTON_STATES {ACTIVE, CANCEL_JOB, INACTIVE}
    private ShipTypesOfFleetComponent.BUTTON_STATES currentButtonState = ShipTypesOfFleetComponent.BUTTON_STATES.ACTIVE;
    private boolean updateButtonState = true;
    private ObservableList<Jobs.Job> shipJobs;

    @Inject
    public ShipTypesOfFleetComponent(FleetManagerComponent fleetManagerComponent, ResourcesService resourcesService, ShipService shipService, Subscriber subscriber){
        this.fleetManagerComponent = fleetManagerComponent;
        this.resourcesService = resourcesService;
        this.shipService = shipService;
        this.subscriber = subscriber;
    }

    @OnInit
    public void addRunnable() {
        this.resourcesService.setOnResourceUpdates(this::setBuildButton);
    }

    public void setBuildButton() {
        if (this.updateButtonState) {
            if (Objects.nonNull(shipService.getNeededResources(shipType))) {
                if (resourcesService.hasEnoughResources(shipService.getNeededResources(shipType))) {
                    this.currentButtonState = ShipTypesOfFleetComponent.BUTTON_STATES.ACTIVE;
                    this.buildShipButton.setDisable(false);
                } else {
                    this.currentButtonState = ShipTypesOfFleetComponent.BUTTON_STATES.INACTIVE;
                    this.buildShipButton.setDisable(true);
                }
            }
            //Todo: Check Number of shipyards on island
        }
    }

//    @Inject
//    public void setShipJobUpdates() {
//        this.jobsService.onJobsLoadingFinished("ship", this::setShipFinishers);
//        this.jobsService.onJobsLoadingFinished(() ->
//                this.shipJobs = this.jobsService.getJobObservableListOfType("ship"));
//    }

    public void setItem(Ships.BlueprintInFleetDto blueprintInFleetDto){
        this.typeLabel.setText(blueprintInFleetDto.type());
        this.sizeLabel.setText(String.valueOf(blueprintInFleetDto.count()));
        this.shipType = blueprintInFleetDto.type();
        this.fleet = blueprintInFleetDto.fleet();
    }

    public void buildShip(){
        this.subscriber.subscribe(this.shipService.beginShipJob(this.fleet._id(), this.shipType, this.fleet.location()),
            job->{
                System.out.println("ship job has started");
            },
            error -> System.out.println("Error while trying to create a new ship job in ShipTypesOfFleetComponent:\n" + error.getMessage()));
    }

    public void decrementSize(){}

    public void incrementSize(){}
}

