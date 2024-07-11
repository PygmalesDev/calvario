package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "IslandClaiming.fxml")
public class IslandClaimingComponent extends Pane {
    @FXML
    Text islandTypeText;
    @FXML
    Text colonizersText;
    @FXML
    Text timeText;
    @FXML
    Text capacityText;
    @FXML
    ImageView capacityImage;
    @FXML
    ImageView colonizersImage;
    @FXML
    ImageView timerImage;
    @FXML
    Button exploreButton;

    @Inject
    JobsService jobsService;
    @Inject
    ImageCache imageCache;
    @Inject
    IslandsService islandsService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private Island currentIsland;

    @Inject
    public IslandClaimingComponent() {}

    @OnRender
    public void render() {
        this.timerImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/capacity_icon.png"));
        this.capacityImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/capacity_icon.png"));
        this.colonizersImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/crewmates_icon.png"));
    }

    public void setIslandInformation(Island island) {
        this.currentIsland = island;
        this.islandTypeText.setText(this.gameResourceBundle.getString(islandTranslation.get(island.type().toString())));
        this.capacityText.setText(String.valueOf(island.resourceCapacity()));
        this.colonizersText.setText(String.valueOf(island.crewCapacity()));
        this.timeText.setText("?");
    }

    public void exploreIsland() {
        this.islandsService.claimIsland(this.currentIsland);
    }
}
