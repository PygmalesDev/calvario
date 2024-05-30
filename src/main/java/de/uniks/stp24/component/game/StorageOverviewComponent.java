package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "StorageOverview.fxml")
public class StorageOverviewComponent extends VBox {
    @FXML
    public Button closeStorageOverviewButton;
    @FXML
    public ListView resourceListView;
    @FXML
    public HBox economyButton;
    @FXML
    public Button populationButton;
    @FXML
    public Button productionButton;
    @FXML
    public Button tacticsButton;
    @FXML
    Label empireNameLabel;

    @Inject
    public StorageOverviewComponent() {}


    public void showPopulation(){

    }


    public void showEconomy(){

    }
    public void showProduction(){}
    public void showTactics(){}


    public void closeStorageOverview(){
        this.getParent().setVisible(false);
    }


}
