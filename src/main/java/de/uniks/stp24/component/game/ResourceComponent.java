package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.resourceTranslation;

@Component(view = "Resource.fxml")
public class ResourceComponent extends HBox implements ReusableItemComponent<Resource> {
    @FXML
    public ImageView resourceIconImageView;
    @FXML
    public Text countText;
    @FXML
    public Text descriptionText;
    @FXML
    public Text changePerSeasonText;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    ResourceBundle langBundle;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    @Inject
    ImageCache imageCache;

    final boolean showCount;
    final boolean showName;
    final boolean showIcon;
    final boolean showChangePerSeason;
    private String type = "";

    @Inject
    public ResourceComponent(boolean showCount, boolean showName, boolean showIcon, boolean showChangePerSeason,
                             ResourceBundle gameResourceBundle, ImageCache imageCache) {
        this.showCount = showCount;
        this.showName = showName;
        this.showIcon = showIcon;
        this.showChangePerSeason = showChangePerSeason;
        this.gameResourceBundle = gameResourceBundle;
        this.imageCache = imageCache;
    }


    public ResourceComponent(String type, ResourceBundle gameResourceBundle, ImageCache imageCache) {
        this.showCount = true;
        this.showName = false;
        this.showIcon = true;
        this.showChangePerSeason = false;
        this.gameResourceBundle = gameResourceBundle;
        this.type = type;
        this.imageCache = imageCache;
    }

    @Override
    public void setItem(@NotNull Resource resource) {
        if (showName) {
            String name = gameResourceBundle.getString(resourceTranslation.get(resource.resourceID()));
            descriptionText.setText(name);
            descriptionText.setVisible(true);
        } else {
            descriptionText.setVisible(false);
        }

        countText.setVisible(showCount);
        switch (type) {
            case "positive" -> countText.setText("+" + resource.count());
            case "negative" -> countText.setText("-" + resource.count());
            default -> countText.setText("x" + resource.count());
        }

        if (showIcon) {
            resourceIconImageView.setImage(imageCache.get("icons/resources/" + resource.resourceID() + ".png"));
            resourceIconImageView.setVisible(true);
        } else {
            resourceIconImageView.setVisible(false);
        }

        if (showChangePerSeason) {
            if (resource.changePerSeason() == 0) {
                changePerSeasonText.setVisible(false);
            } else {
                String sign;
                sign = (resource.changePerSeason() > 0) ? "+" : "";
                changePerSeasonText.setText(sign + resource.changePerSeason());
                changePerSeasonText.setVisible(true);
            }
        } else {
            changePerSeasonText.setVisible(false);
        }
    }

}
