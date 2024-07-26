package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.ImageCache;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javax.inject.Inject;
import java.util.Objects;

public class FogOfWar {
    @Inject
    ImageCache imageCache;

    Image fogImage;
    ImagePattern fogPattern;

    InGameController inGameController;
    double x, y;


    Shape originalFog;
    Shape currentFog;
    Shape removedFog;

    @Inject
    public FogOfWar() {

    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void setMapSize(double mapWidth, double mapHeight) {
        this.x = mapWidth;
        this.y = mapHeight;
    }

    public void init() {
        this.originalFog = new Rectangle(0, 0, x, y);
        this.fogImage = new Image("/de/uniks/stp24/assets/backgrounds/fog.png");
        this.fogPattern = new ImagePattern(this.fogImage, 0, 0, this.fogImage.getWidth()*2, this.fogImage.getHeight()*2, false);
        this.originalFog.setFill(this.fogPattern);
        this.currentFog = this.originalFog;
    }

    public Shape subtract(Shape toRemove) {
        if (Objects.nonNull(this.removedFog))
            this.removedFog = Shape.union(this.removedFog, toRemove);
        else
            this.removedFog = toRemove;
        this.currentFog = Shape.subtract(this.originalFog, removedFog);
        this.currentFog.setFill(this.fogPattern);

        this.currentFog.setStroke(Color.GRAY);
        this.currentFog.setStrokeWidth(10);
//        this.currentFog.


        return currentFog;
    }

    public Shape getFog() {
        return currentFog;
    }
}
