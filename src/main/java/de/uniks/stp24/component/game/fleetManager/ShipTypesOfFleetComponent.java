package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.service.game.FleetService;
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

import static de.uniks.stp24.model.Fleets.Fleet;

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
    private final FleetManagerComponent fleetManagerComponent;

    private Ships.BlueprintInFleetDto blueprintInFleetDto;
    private enum BUTTON_STATES {ACTIVE, CANCEL_JOB, INACTIVE}
    private ShipTypesOfFleetComponent.BUTTON_STATES currentButtonState = ShipTypesOfFleetComponent.BUTTON_STATES.ACTIVE;
    private boolean updateButtonState = true;
    private ObservableList<Jobs.Job> shipJobs;

    @Inject
    public ShipTypesOfFleetComponent(FleetManagerComponent fleetManagerComponent, ResourcesService resourcesService, ShipService shipService, Subscriber subscriber, FleetService fleetService){
        this.fleetManagerComponent = fleetManagerComponent;
        this.resourcesService = resourcesService;
        this.shipService = shipService;
        this.subscriber = subscriber;
        this.fleetService = fleetService;
    }

    @OnInit
    public void addRunnable() {
        this.resourcesService.setOnResourceUpdates(this::setBuildButton);
    }

    public void setBuildButton() {
        if (this.updateButtonState) {
            if (Objects.nonNull(shipService.getNeededResources(blueprintInFleetDto.type()))) {
                if (resourcesService.hasEnoughResources(shipService.getNeededResources(blueprintInFleetDto.type()))) {
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

    public void buildShip(){
        this.subscriber.subscribe(this.shipService.beginShipJob(this.blueprintInFleetDto.fleet()._id(), this.blueprintInFleetDto.type(), this.blueprintInFleetDto.fleet().location()),
            job->{
                //Todo: remove print
                System.out.println("ship job has started");
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
                    this.sizeLabel.setText(this.sizeLabel.getText().replaceAll("/.*", "/" + dto.size().get(this.blueprintInFleetDto.type())));
                    this.fleetManagerComponent.setCommandLimit(dto);
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

