package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "MarketSeasonComponent.fxml")
public class MarketSeasonComponent extends HBox implements ReusableItemComponent<SeasonComponent> {
    @FXML
    Text transActionTypeText;
    @FXML
    ImageView resourceTypeImageView;
    @FXML
    Text resourceAmountText;
    @FXML
    AnchorPane moneyIconAnchorPane;
    @FXML
    Text moneyAmountText;
    @FXML
    ToggleButton playControlsButton;
    @FXML
    Button cancelTradesButton;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    ResourceBundle langBundle;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    @Inject
    ImageCache imageCache;

    @Inject
    public MarketSeasonComponent() {

    }

    @Override
    public void setItem(@NotNull SeasonComponent seasonComponent) {
        transActionTypeText.setText(seasonComponent.transActionTypeText());
        resourceTypeImageView.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + seasonComponent.resourceType() + ".png"));
        if(seasonComponent.transActionTypeText().equals("buy")){
            resourceAmountText.setText("+" + seasonComponent.resourceAmount());
            moneyAmountText.setText("-"+ seasonComponent.moneyAmount());
        } else {
            int sell = seasonComponent.resourceAmount() * -1;
            moneyAmountText.setText("+"+ sell);
            resourceAmountText.setText("-" + seasonComponent.moneyAmount());
        }
    }
}
