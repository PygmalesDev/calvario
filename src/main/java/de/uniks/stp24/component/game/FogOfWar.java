package de.uniks.stp24.component.game;

import de.uniks.stp24.service.ImageCache;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
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

    double x, y;

    Shape originalFog;
    Shape currentFog;
    Shape removedFog;

    @Inject
    public FogOfWar() {

    }

    public void setMapSize(double mapWidth, double mapHeight) {
        this.x = mapWidth;
        this.y = mapHeight;
    }

    public void init() {
        this.originalFog = new Rectangle(0, 0, x, y);
        this.fogImage = new Image("/de/uniks/stp24/assets/backgrounds/fog.jpg");
        this.fogPattern = new ImagePattern(this.fogImage, 0, 0, this.fogImage.getWidth()*2, this.fogImage.getHeight()*2, false);
        this.currentFog = this.originalFog;
        this.updateFog();
    }

    public void removeShapesFromFog(Shape... toRemoves) {
        for (Shape shape : toRemoves) {
            this.updateRemovedFog(shape);
        }

        this.updateFog();
    }

    private void updateRemovedFog(Shape shape) {
        if (Objects.nonNull(shape)) {
            if (Objects.nonNull(this.removedFog))
                this.removedFog = Shape.union(this.removedFog, shape);
            else
                this.removedFog = shape;
        }
    }

    private void updateFog() {
        if (Objects.nonNull(this.removedFog))
            this.currentFog = Shape.subtract(this.originalFog, this.removedFog);

        this.currentFog.setFill(this.fogPattern);
//        ColorAdjust colorAdjust = new ColorAdjust();
//        colorAdjust.setHue(0.5);
//        colorAdjust.setSaturation(1.0);
//        this.currentFog.setEffect(colorAdjust);
    }

    public Shape getFog() {
        return currentFog;
    }
}
