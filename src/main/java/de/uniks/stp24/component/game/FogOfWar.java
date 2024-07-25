package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.ImageCache;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javax.inject.Inject;

public class FogOfWar {
    @Inject
    ImageCache imageCache;

    Image fogImage;
    ImagePattern fogPattern;

    InGameController inGameController;
    double x, y;

    Shape fog;

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
        this.fog = new Rectangle(0, 0, x, y);
        this.fogImage = new Image("/de/uniks/stp24/assets/backgrounds/fog.png");
        this.fogPattern = new ImagePattern(this.fogImage, 0, 0, this.fogImage.getWidth(), this.fogImage.getHeight(), false);
        this.fog.setFill(this.fogPattern);
    }

    public Shape subtract(Shape shape) {
        this.fog = Shape.subtract(this.fog, shape);
        this.fog.setFill(this.fogPattern);
        return fog;
    }

    public Shape getFog() {
        return fog;
    }
}
