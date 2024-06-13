package de.uniks.stp24.component.game;


import de.uniks.stp24.App;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "StorageOverview.fxml")
public class StorageOverviewComponent extends VBox {
    @FXML
    Button closeStorageOverviewButton;
    @FXML
    public ListView<Resource> resourceListView;
    @FXML
    Label empireNameLabel;


    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    ResourcesService resourcesService;
    @Inject
    EmpireService empireService;
    @Inject
    EventListener eventListener;
    @Inject
    TokenStorage tokenStorage;

    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private String lastUpdate;
    private String lastSeasonUpdate;
    Provider<ResourceComponent> resourceComponentProvider = () -> new ResourceComponent(true, true, true, true, gameResourceBundle);


    @Inject
    public StorageOverviewComponent() {
        lastUpdate = "";
        lastSeasonUpdate = "";
    }

    @OnInit
    public void init() {
        if(!tokenStorage.isSpectator()) {
            createEmpireListener();
            createSeasonListener();
        }
    }

    /**
     * Initialising the resource list
     **/
    @OnRender
    public void initStorageList() {
        if(!tokenStorage.isSpectator()) {
            this.resourceListView.setSelectionModel(null);
            this.subscriber.subscribe(this.empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()), empireDto -> resourceListGeneration(empireDto, null));
            this.resourceListView.setCellFactory(list -> new ComponentListCell<>(app, resourceComponentProvider));
        }
    }
    //Todo: changePerSeason


    private void resourceListGeneration(EmpireDto empireDto, AggregateItemDto[] aggregateItems) {
        Map<String, Integer> resourceMap = empireDto.resources();
        ObservableList<Resource> resourceList = resourcesService.generateResourceList(resourceMap, resourceListView.getItems(), aggregateItems);
        this.resourceListView.setItems(resourceList);
    }


    /**
     * Listener for the empire: Changes of the resources will change the list in the storage overview.
     **/
    public void createEmpireListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    if (!lastUpdate.equals(event.data().updatedAt())) {
                        resourceListGeneration(event.data(), null);
                        this.lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorListener"));
    }

    /**
     * Listener for the season: change per season of a resource will be updated
     **/
    public void createSeasonListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {
                    if (!lastSeasonUpdate.equals(event.data().updatedAt())) {
                        System.out.println("season changed and was not updated");
                        subscriber.subscribe(empireService.getEmpire(tokenStorage.getGameId(), tokenStorage.getEmpireId()), empireDto -> subscriber.subscribe(empireService.getResourceAggregates(tokenStorage.getGameId(), tokenStorage.getEmpireId()), aggregateResultDto -> {
                            resourceListGeneration(empireDto, aggregateResultDto.items());
                        }));
                        this.lastSeasonUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorListener"));
    }

    public void closeStorageOverview() {
        this.getParent().setVisible(false);
    }

    @OnDestroy
    void destroy() {
        this.subscriber.dispose();
    }

}
