package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.awt.*;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.resourceTranslation;

@Component(view = "MarketResourceComponent.fxml")
public class MarketResourceComponent extends VBox implements ReusableItemComponent<Resource> {
    @FXML
    Button resourceIconButton;
    @FXML
    ImageView resourceIcon;
    @FXML
    Text resourceAmount;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    ResourceBundle langBundle;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;


    ImageCache imageCache = new ImageCache();
    boolean showIcon;
    boolean showCount;
    boolean showButton;

    @Inject
    public MarketResourceComponent(boolean showIcon, boolean showCount, boolean showButton, ResourceBundle gameResourceBundle) {
        this.showCount = showCount;
        this.showIcon = showIcon;
        this.showButton = showButton;
        this.gameResourceBundle = gameResourceBundle;
    }

    @Override
    public void setItem(@NotNull Resource resource) {
        if (showCount) {
            resourceAmount.setText("x" + resource.count());
            resourceAmount.setVisible(true);
        } else {
            resourceAmount.setVisible(false);
        }

        if (showIcon) {
            resourceIcon.setImage(imageCache.get("icons/resources/" + resource.resourceID() + ".png"));
            resourceIcon.setVisible(true);
        } else {
            resourceIcon.setVisible(false);
        }



    }
}
