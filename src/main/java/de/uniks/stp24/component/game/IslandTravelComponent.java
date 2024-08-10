package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ShipService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Objects;

@Component(view = "IslandTravel.fxml")
public class IslandTravelComponent extends Pane {
    @FXML
    SplitPane travelButtonControlPane;
    @FXML
    Button travelButton;

    private String islandID = "";

    private final Tooltip travelTooltip = new Tooltip();
    private final Duration TOOLTIP_ANIMATION_DURATION = Duration.seconds(0.1);

    @Inject
    FleetCoordinationService fleetCoordinationService;
    @Inject
    FleetService fleetService;
    @Inject
    JobsService jobsService;
    @Inject
    Subscriber subscriber;
    @Inject
    ShipService shipService;

    @Inject
    public IslandTravelComponent() {
    }

    public void setIslandInformation(Island island) {
        this.islandID = island.id();
        this.setTravelInformation(this.fleetCoordinationService.getSelectedFleet());
    }

    @OnRender
    public void setOnFleetSelected() {
        this.travelTooltip.setHideDelay(TOOLTIP_ANIMATION_DURATION);
        this.travelTooltip.setShowDelay(TOOLTIP_ANIMATION_DURATION);
        this.travelTooltip.getStyleClass().add("controlTooltip");

        this.fleetCoordinationService.onFleetSelected(this::setTravelInformation);
    }

    public void travelToIsland() {
        this.fleetCoordinationService.travelToIsland(this.islandID);
        this.travelButton.setDisable(true);
    }

    public void setTravelInformation(Fleet fleet) {
        if (Objects.isNull(this.islandID)) return;

        this.travelButton.setDisable(true);
        this.travelButtonControlPane.setTooltip(this.travelTooltip);

        if (Objects.nonNull(fleet)) {
            if (!this.islandID.equals(fleet.location())) {
                this.fleetCoordinationService.generateTravelPaths(fleet.location(), this.islandID);
                if (this.jobsService.getJobObservableListOfType("travel")
                        .filtered(job -> job.fleet().equals(fleet._id())).isEmpty()) {
                    this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()), result -> {
                        if (result.length != 0) {
                            this.travelButton.setDisable(false);
                            this.travelButtonControlPane.setTooltip(null);
                        } else this.travelTooltip.setText("This fleet has no ships for travel!");
                    }, error -> System.out.printf("Caught an error while trying to retrieve ships " +
                            "of the fleet in IslandClaimingComponent:\n %s", error.getMessage()));
                } else this.travelTooltip.setText("This fleet is already traveling! Stop it's travel and try again!");
            } else this.travelTooltip.setText("The fleet is already parked on this island!");
        } else this.travelTooltip.setText("Select the fleet first!");
    }
}

