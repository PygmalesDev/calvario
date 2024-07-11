package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.SeasonComponent;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    Button playControlsButton;
    @FXML
    Button cancelTradesButton;
//    @Inject
//    @org.fulib.fx.annotation.controller.Resource
//    ResourceBundle langBundle;
//    @Inject
//    @Named("gameResourceBundle")
//    ResourceBundle gameResourceBundle;
    @Inject
    ImageCache imageCache;

    @Inject
    public MarketSeasonComponent() {

    }

    @Override
    public void setItem(@NotNull SeasonComponent seasonComponent) {
        System.out.println(seasonComponent);
        transActionTypeText.setText(seasonComponent.transActionTypeText());
        resourceTypeImageView.setImage(imageCache.get(seasonComponent.resourceType()));
        resourceAmountText.setText(String.valueOf(seasonComponent.resourceAmount()));
        moneyAmountText.setText(String.valueOf(seasonComponent.moneyAmount()));
    }
}
