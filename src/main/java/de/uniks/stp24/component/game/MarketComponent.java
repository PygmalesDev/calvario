package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;

@Component(view = "MarketComponent.fxml")
public class MarketComponent extends StackPane {
    @FXML
    Button closeMarketOverviewButton;
    @FXML
    Label userCredits;
    @FXML
    Label marketFee;
    @FXML
    Label numberOfGoods;
    @FXML
    Label priceTag;
    @FXML
    Button everySeasonButton;
    @FXML
    Button sellButton;
    @FXML
    Button buyButton;
    @FXML
    Button incrementNumberOfGoods;
    @FXML
    Button decrementNumberOfGoods;
    @FXML
    Label gemmyAlloysImage;
    @FXML
    Label rumImage;
    @FXML
    Label sparklingGeodeImage;
    @FXML
    Label gunPowderImage;
    @FXML
    Label coalImage;
    @FXML
    Label provisionImage;
    @FXML
    Label gemmyAlloysNumber;
    @FXML
    Label rumNumber;
    @FXML
    Label sparklingGeodeNumber;
    @FXML
    Label gunPowderNumber;
    @FXML
    Label coalNumber;
    @FXML
    Label provisionNumber;


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