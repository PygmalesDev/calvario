package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.dto.ShortSystemDto;
import de.uniks.stp24.model.EffectSource;
import de.uniks.stp24.model.Fleets;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component(view = "NewFleet.fxml")
public class NewFleetComponent extends VBox {
    @FXML
    public Label islandNameLabel;

    @Inject
    IslandsService islandsService;
    @Inject
    FleetService fleetService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;

    private FleetManagerComponent fleetManagerComponent;

    private int islandNameIndex = 0;
    private List<ShortSystemDto> islandList = new ArrayList<>();

    @Inject
    public NewFleetComponent() {
    }

    public void setFleetManager(FleetManagerComponent fleetManagerComponent){
        this.fleetManagerComponent = fleetManagerComponent;
    }

    public void close() {
        this.setVisible(false);
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

    public void createNewFleet() {
        this.setVisible(true);
        this.islandList.addAll(this.islandsService.getDevIsles());
        this.islandList = this.islandList.stream()
                .filter(shortSystemDto -> shortSystemDto.owner().equals(this.tokenStorage.getEmpireId()) && shortSystemDto.buildings().contains("shipyard"))
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
                    close();
                    this.fleetManagerComponent.showFleets();
                },
                error -> System.out.println("Error while creating a new fleet in the NewFleetComponent:\n" + error.getMessage())
        );
    }

    @OnDestroy
    public void destroy(){
        this.subscriber.dispose();
    }
}
