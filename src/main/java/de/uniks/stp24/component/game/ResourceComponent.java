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
import java.util.ResourceBundle;

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


    ImageCache imageCache = new ImageCache();
    boolean showCount;
    boolean showName;
    boolean showIcon;
    boolean showChangePerSeason;

    @Inject
    public ResourceComponent(boolean showCount, boolean showName, boolean showIcon, boolean showChangePerSeason) {
        this.showCount = showCount;
        this.showName = showName;
        this.showIcon = showIcon;
        this.showChangePerSeason = showChangePerSeason;
    }

    //Todo: resourceID in translations, imagePaths, constants

    @Override
    public void setItem(@NotNull Resource resource){
        if(showName){
            descriptionText.setText(resource.resourceID());
        }else{
            descriptionText.setVisible(false);
        }

        if(showCount){
            countText.setText("x" + resource.count());
        }else{
            countText.setVisible(false);
        }

        if(showIcon){
            resourceIconImageView.setImage(imageCache.get("icons/Resources/" + resource.resourceID() + ".png"));
        }else{
            resourceIconImageView.setVisible(false);
        }

        if(showChangePerSeason){
            if(resource.changePerSeason() == 0){
                changePerSeasonText.setVisible(false);
            }else {
                String sign;
                sign = (resource.changePerSeason() > 0) ? "+" : "-";
                changePerSeasonText.setText(sign + resource.changePerSeason());
            }
        }else{
            changePerSeasonText.setVisible(false);
        }
    }

}
