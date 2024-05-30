package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

@Component(view = "ResourcesOverview.fxml")
public class ResourcesOverviewComponent extends VBox {
    public Button closeProductionOverviewButton;
    public ListView resourceListView;
    public HBox economyButton;
    public Button populationButton;
    public Button productionButton;
    public Button tacticsButton;
    @FXML
    Label empireNameLabel;


    public void showPopulation(){

    }


    public void showEconomy(){

    }
    public void showProduction(){}
    public void showTactics(){}

    public void closeProductionOverview(){

    }


}
