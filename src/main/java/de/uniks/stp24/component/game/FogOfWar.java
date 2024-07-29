package de.uniks.stp24.component.game;

import de.uniks.stp24.service.ImageCache;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Random;

import static de.uniks.stp24.service.Constants.ISLAND_HEIGHT;
import static de.uniks.stp24.service.Constants.ISLAND_WIDTH;


public class FogOfWar {
    @Inject
    ImageCache imageCache;

    Image fogImage;
    ImagePattern fogPattern;

    double x, y;
    double islandScale = 1.25;

    Random random = new Random();


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

    public void removeShapesFromFog(IslandComponent island, Shape... toRemoves) {
        for (Shape shape : toRemoves) this.updateRemovedFog(shape);
        if (Objects.nonNull(island)) this.updateRemovedFog(this.randomFogAroundIsland(island));
        this.updateFog();
    }

    private void updateRemovedFog(Shape shape) {
        if (Objects.nonNull(shape))
            this.removedFog = Objects.nonNull(this.removedFog) ? Shape.union(this.removedFog, shape) : shape;
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

    public Shape randomFogAroundIsland(IslandComponent island) {
        Shape toRemoveShape = null;

        int count = random.nextInt(30, 50);
        int xRadius;
        int yRadius;
        int distance;
        double angle;
        Ellipse ellipse;

        for (int i = 0; i < count; i++) {
            angle = random.nextInt(360)*Math.PI/180;
            xRadius =  random.nextInt(25, 50);
            yRadius =  random.nextInt(25, 50);
            distance =  random.nextInt(125, 150);
            ellipse = new Ellipse(
                    island.getPosX() + ISLAND_WIDTH/2 * islandScale + 17 +  distance*Math.cos(angle),
                    island.getPosY() + ISLAND_HEIGHT/2 * islandScale + 7 + distance*Math.sin(angle),
                    xRadius, yRadius);
            toRemoveShape = Objects.nonNull(toRemoveShape) ? Shape.union(toRemoveShape, ellipse) : ellipse;
        }

        return toRemoveShape;
    }

    public Shape getCurrentFog() {
        return this.currentFog;
    }
}
