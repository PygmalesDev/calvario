package de.uniks.stp24.component.game;

import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "MarketComponent.fxml")
public class MarketComponent extends StackPane {

    @Inject
    public MarketComponent() {
    }

    public void closeMarketOverview() {
        this.getParent().setVisible(false);
    }

}
