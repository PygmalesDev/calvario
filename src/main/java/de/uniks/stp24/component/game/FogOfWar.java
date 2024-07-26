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
    Shape addedFog;

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
        this.originalFog.setFill(this.fogPattern);
        this.currentFog = this.originalFog;
    }

    public void changeFog(Shape toRemove, Shape toAdd) {
        if (Objects.nonNull(toRemove))
            removeFromFog(toRemove);

        if (Objects.nonNull(toAdd))
            addToFog(toAdd);

        updateFog();
    }

    private void removeFromFog(Shape toRemove) {
        if (Objects.nonNull(this.removedFog))
            this.removedFog = Shape.union(this.removedFog, toRemove);
        else
            this.removedFog = toRemove;
    }

    private void addToFog(Shape toAdd) {
        if (Objects.nonNull(this.addedFog))
            this.addedFog = Shape.union(this.addedFog, toAdd);
        else
            this.addedFog = toAdd;
    }

    private void updateFog() {
        if (Objects.nonNull(this.removedFog))
            this.currentFog = Shape.subtract(this.originalFog, this.removedFog);

        if (Objects.nonNull(this.addedFog))
            this.currentFog = Shape.union(this.originalFog, this.addedFog);

        this.currentFog.setFill(this.fogPattern);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setHue(0.5);
        colorAdjust.setSaturation(1.0);
        this.currentFog.setEffect(colorAdjust);
    }

    public Shape getFog() {
        return currentFog;
    }
}
