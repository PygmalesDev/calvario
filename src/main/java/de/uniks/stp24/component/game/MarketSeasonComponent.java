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
    private SeasonComponent seasonComponent;

    @Inject
    public MarketSeasonComponent() {

    }

    @Override
    public void setItem(@NotNull SeasonComponent seasonComponent) {
        this.seasonComponent = seasonComponent;

        transActionTypeText.setText(this.seasonComponent.getTransActionTypeText());
        resourceTypeImageView.setImage(imageCache.get("/de/uniks/stp24/icons/resources/" + this.seasonComponent.getResourceType() + ".png"));
        if(this.seasonComponent.getTransActionTypeText().equals("buy")){
            resourceAmountText.setText("+" + this.seasonComponent.getResourceAmount());
            moneyAmountText.setText("-"+ this.seasonComponent.getMoneyAmount());
        } else {
            int sell = this.seasonComponent.getResourceAmount() * -1;
            moneyAmountText.setText("+"+ sell);
            resourceAmountText.setText("-" + this.seasonComponent.getMoneyAmount());
        }

        playControlsButton.setSelected(this.seasonComponent.isPlaying());
        playControlsButton.setOnAction(event -> this.seasonComponent.setPlaying(playControlsButton.isSelected()));
    }
}
