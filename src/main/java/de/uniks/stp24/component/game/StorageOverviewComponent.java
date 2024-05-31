package de.uniks.stp24.component.game;


import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Provider;

@Component(view = "StorageOverview.fxml")
public class StorageOverviewComponent extends VBox {
    @FXML
    Button closeStorageOverviewButton;
    @FXML
    ListView<Resource> resourceListView;
    @FXML
    HBox economyButton;
    @FXML
    Button populationButton;
    @FXML
    Button productionButton;
    @FXML
    Button tacticsButton;
    @FXML
    Label empireNameLabel;

    //@Inject
    //Provider<ResourceComponent> resourceComponentProvider;
    @Inject
    Subscriber subscriber;
    @Inject
    ResourcesService resourcesService;

    private ObservableList<Resource> resourceList = FXCollections.observableArrayList();

    @Inject
    public StorageOverviewComponent() {}


    @OnInit
    void init(){
        //subscriber.subscribe(empireApiService.getEmpire("gamename", "empirename"));
    }

    @OnRender
    void render(){
        resourceListView.setItems(resourceList);
    }


    public void showPopulation(){

    }


    public void showEconomy(){

    }
    public void showProduction(){}
    public void showTactics(){}


    public void closeStorageOverview(){
        this.getParent().setVisible(false);
    }


    @OnDestroy
    void destroy(){
        subscriber.dispose();
    }


}
