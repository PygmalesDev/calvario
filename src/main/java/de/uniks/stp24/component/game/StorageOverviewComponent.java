package de.uniks.stp24.component.game;


import de.uniks.stp24.App;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Resource;
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
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

@Component(view = "StorageOverview.fxml")
public class StorageOverviewComponent extends VBox {
    @FXML
    Button closeStorageOverviewButton;
    @FXML
    ListView<Resource> resourceListView;
    @FXML
    Label empireNameLabel;

    Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, true, true, true);

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

    private String lastUpdate;
    private String gameID;
    private String empireID;
    private Map<String, Integer> resourcesLastSeasonChange;

    @Inject
    public StorageOverviewComponent() {
        lastUpdate = "";
    }


    /** Initialising the resource list **/
    public void initStorageList(){
        this.subscriber.subscribe(this.empireService.getEmpire(gameID, empireID), this::resourceListGeneration);
    }


    @OnRender
    void render(){
        this.resourceListView.setCellFactory(list -> new ComponentListCell<>(app, resourceComponentProvider));
        //Todo: changePerSeason
    }

    private void resourceListGeneration(EmpireDto empireDto){
        Map<String, Integer> resourceMap = empireDto.resources();
        ObservableList<Resource> resourceList = resourcesService.generateResourceList(resourceMap, resourceListView.getItems());
        this.resourceListView.setItems(resourceList);
    }


    /** Listener for the empire: Changes of the resources will change the list in the storage overview.**/
    public void createEmpireListener(){
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + this.gameID + ".empires." + this.empireID + ".updated", EmpireDto.class),
                event -> {
                    if(!lastUpdate.equals(event.data().updatedAt())){
                        resourceListGeneration(event.data());
                        this.lastUpdate = event.data().updatedAt();
                    }},
                error -> {
                    System.out.println("errorListener");
                });
    }





    public void closeStorageOverview(){
        this.getParent().setVisible(false);
    }


    public void setGameID(String gameID) {this.gameID = gameID;}
    public void setEmpireID(String empireID) {this.empireID = empireID;}


    @OnDestroy
    void destroy(){
        this.subscriber.dispose();
    }


}
