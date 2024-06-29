package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "MarketComponent.fxml")
public class MarketComponent extends StackPane {
    @FXML
    Button closeMarketOverviewButton;

    @Inject
    public MarketComponent() {
    }

    public void closeMarketOverview() {
        this.getParent().setVisible(false);
    }

}
