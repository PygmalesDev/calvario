package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;

@Component(view = "MarketComponent.fxml")
public class MarketComponent extends StackPane {
    @FXML
    Button closeMarketOverviewButton;

    private StorageOverviewComponent storageOverviewComponent;

    @Inject
    public MarketComponent() {

    }

    @OnInit
    public void init() {
    }

    public void setStorageOverviewController(StorageOverviewComponent storageOverviewComponent) {
        this.storageOverviewComponent = storageOverviewComponent;
    }

    public void closeMarketOverview() {
        this.getParent().setVisible(false);
    }
}