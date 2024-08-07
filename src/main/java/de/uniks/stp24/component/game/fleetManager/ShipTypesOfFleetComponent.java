package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.service.game.*;
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
    @FXML
    public Button decrementSizeButton;

    public final ResourcesService resourcesService;
    public final ShipService shipService;
    private final FleetService fleetService;
    public final Subscriber subscriber;
    public final JobsService jobService;
    public final IslandsService islandsService;
    private final FleetManagerComponent fleetManagerComponent;

    private Ships.BlueprintInFleetDto blueprintInFleetDto;
    private ObservableList<Job> shipJobs;

    @Inject
    public ShipTypesOfFleetComponent(FleetManagerComponent fleetManagerComponent, ResourcesService resourcesService, ShipService shipService, Subscriber subscriber, FleetService fleetService){
        this.fleetManagerComponent = fleetManagerComponent;
        this.resourcesService = resourcesService;
        this.shipService = shipService;
        this.subscriber = subscriber;
        this.fleetService = fleetService;
        this.islandsService = fleetManagerComponent.islandsService;
        this.jobService = fleetManagerComponent.jobsService;
    }

    @OnInit
    public void addRunnable() {
        this.resourcesService.setOnResourceUpdates(this::setBuildButton);
    }

    public void setBuildButton() {
        if (Objects.nonNull(shipService.getNeededResources(blueprintInFleetDto.type()))) {
            if (resourcesService.hasEnoughResources(shipService.getNeededResources(blueprintInFleetDto.type()))) {
                this.buildShipButton.setDisable(false);
            } else {
                this.buildShipButton.setDisable(true);
            }
        }
        int numberOfShipJobs = shipJobsOnIsland();
        int numberOfShipYards = this.islandsService.getIslandComponent(blueprintInFleetDto.fleet().location()).getIsland().buildings().stream()
                .filter("shipyard"::equals).toList().size();
        if (numberOfShipJobs >= numberOfShipYards) {
            this.buildShipButton.setDisable(true);
        }

    }

    public void setItem(Ships.BlueprintInFleetDto blueprintInFleetDto){
        this.typeLabel.setText(blueprintInFleetDto.type());
        int plannedSize = 0;
        if(blueprintInFleetDto.fleet().size().get(blueprintInFleetDto.type()) != null) {
            plannedSize = blueprintInFleetDto.fleet().size().get(blueprintInFleetDto.type());
        }
        this.sizeLabel.setText(blueprintInFleetDto.count() + "/" + plannedSize);
        this.blueprintInFleetDto = blueprintInFleetDto;
        if (blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) == 0){
            this.decrementSizeButton.setDisable(true);
        }
        if(blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) == 1 && this.blueprintInFleetDto.count() != 0) {
            this.decrementSizeButton.setDisable(true);
        }
        this.setBuildButton();
    }

    public int shipJobsOnIsland(){
        return this.jobService.getObservableListForSystem(blueprintInFleetDto.fleet().location()).filtered(job -> job.type().equals("ship")).size();
    }

    public void buildShip() {
        int shipJobsBeforeStart = shipJobsOnIsland();
        this.subscriber.subscribe(this.shipService.beginShipJob(this.blueprintInFleetDto.fleet()._id(), this.blueprintInFleetDto.type(), this.blueprintInFleetDto.fleet().location()),
                job -> {
                    this.fleetManagerComponent.setShipFinisher(job);
                    int shipJobsAfterStart = shipJobsOnIsland();
                    this.fleetManagerComponent.setIslandName(shipJobsAfterStart == shipJobsBeforeStart);
                },
                error -> System.out.println("Error while trying to create a new ship job in ShipTypesOfFleetComponent:\n" + error.getMessage()));
    }


    public void decrementSize(){
        int newSize = 0;
        if(this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) != null) {
            newSize = this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) > 0 ? this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) - 1 : 0;
        }
        editSize(newSize);
    }

    public void incrementSize(){
        int newSize = 1;
        if(this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) != null) {
           newSize = this.blueprintInFleetDto.fleet().size().get(this.blueprintInFleetDto.type()) + 1;
        }
        editSize(newSize);
    }

    public void editSize(int newSize) {
        this.subscriber.subscribe(this.fleetService.editSizeOfFleet(this.blueprintInFleetDto.type(), newSize, this.blueprintInFleetDto.fleet()),
                dto -> {
                    this.sizeLabel.setText(this.sizeLabel.getText().replaceAll("/.*", "/" + dto.size()
                            .get(this.blueprintInFleetDto.type())));
                    this.fleetManagerComponent.setCommandLimit(dto,false);
                    this.decrementSizeButton.setDisable(false);
                    if (dto.size().get(this.blueprintInFleetDto.type()) == 0) {
                        this.decrementSizeButton.setDisable(true);
                        if (this.blueprintInFleetDto.count() == 0) {
                            this.shipService.removeBlueprintFromFleet(blueprintInFleetDto);
                        }
                    }
                    if (dto.size().get(this.blueprintInFleetDto.type()) == 1 && this.blueprintInFleetDto.count() != 0) {
                        this.decrementSizeButton.setDisable(true);
                    }
                },
                error -> System.out.println("Error while changing planned Size in the ShipTypesOfFleetComponent:\n" + error.getMessage()));
    }
}

