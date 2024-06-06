package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;

import javax.inject.Inject;


@Component(view = "IslandComponent.fxml")
public class IslandComponent extends Pane {
    @FXML
    public ImageView rudderImage;
    @FXML
    ImageView islandImage;
    @FXML
    public StackPane flagPane;
    @FXML
    ImageView flagImage;
    @Inject
    ImageCache imageCache;

    private InGameController inGameController;
    private Island island;
    double x, y;
    public boolean islandIsSelected = false;


    // images as placeholder for flags (instead color?)
    final String[] flags = {
            "847.png", "863.png", "911.png", "927.png", "959.png"
    };

    @Inject
    public IslandComponent() {
        if (this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
        this.islandImage.setImage(imageCache.get("icons/isle.jpeg"));
    }

    // an icon should be used depending on island type
    public void applyIcon(IslandType type) {
        String pathToIcon = "icons/islands/";
        pathToIcon += type.name() + ".png";
        this.islandImage.setImage(imageCache.get(pathToIcon));
    }

    public void setFlagImage(int flag) {
        this.flagImage
                .setImage(imageCache.get("test/" + flags[flag]));
    }

    public void applyInfo(Island islandInfo) {
        this.island = islandInfo;
        applyIcon(this.island.type());
    }

    // set double to have only 2 decimals
    public void setPosition(double x, double y) {
        this.x = Math.rint(x * 100.00) / 100.00;
        this.y = Math.rint(y * 100.00) / 100.00;
//        return this;
    }

    public double getPosX() {
//        this.x = island.posX();
        return this.x;
    }

    public double getPosY() {
//          this.y = island.posY();
        return this.y;
    }

    // switch the visibility of all flags
    @OnKey(code = KeyCode.A, shift = true)
    public void showFlag() {
        this.flagPane.setVisible(!this.flagPane.isVisible());
    }

    // by the moment change visibility of flag (image)
    public void showInfo() {
        //TODO show info pane!
        System.out.println(island.type() + " isle at " + x + ", " + y);
        this.flagPane.setVisible(!this.flagPane.isVisible());
    }

    public void showRudder() {
        rudderImage.setVisible(true);

    }

    public void unshowRudder() {
        if (!islandIsSelected) {
            rudderImage.setVisible(false);
        }
    }

    public void showIslandOverview() {
        if (inGameController.selectedIsland != null && inGameController.selectedIsland != this) {
            inGameController.selectedIsland.rudderImage.setVisible(false);
            inGameController.selectedIsland.islandIsSelected = false;
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
            inGameController.selectedIsland = null;
        } else if (inGameController.selectedIsland == this) {
            inGameController.overviewContainer.setVisible(false);
            this.rudderImage.setVisible(false);
            this.islandIsSelected = false;
            this.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
            inGameController.selectedIsland = null;
            return;
        }

        islandIsSelected = true;
        showInfo();
        inGameController.showOverview();
        inGameController.selectedIsland = this;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        islandImage = null;
    }


}