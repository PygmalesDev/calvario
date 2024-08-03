package de.uniks.stp24.component.game;

import de.uniks.stp24.service.ImageCache;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Random;

import static de.uniks.stp24.service.Constants.*;

public class FogOfWar {
    @Inject
    ImageCache imageCache;

    Image fogImage;
    ImagePattern fogPattern;

    double x, y;

    Random random = new Random();

    Shape originalFog;
    Shape currentFog;
    Shape removedFog;
    Shape prevRemovedFog;
    Shape islandFog;

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
        this.updateFog(null);
    }

    public void removeShapesFromFog(IslandComponent island, Shape... toRemoves) {
        this.islandFog = null;
        for (Shape shape : toRemoves) this.updateRemovedFog(shape);
        if (Objects.nonNull(island)) {
            this.updateRemovedFog(new Circle(island.getPosX() + X_OFFSET, island.getPosY() + Y_OFFSET, ISLAND_COLLISION_RADIUS));
            this.updateRemovedFog(this.randomFogAroundIsland(island));
        }
        this.updateFog(island);
    }

    private void updateRemovedFog(Shape shape) {
        if (Objects.nonNull(shape))
            this.removedFog = Objects.nonNull(this.removedFog) ? Shape.union(this.removedFog, shape) : shape;

    }

    private void updateFog(IslandComponent island) {
        if (Objects.nonNull(this.removedFog))
            this.currentFog = Shape.subtract(this.originalFog, this.removedFog);

        if (Objects.nonNull(island)) {
            this.islandFog = new Circle(island.getLayoutX() + X_OFFSET, island.getLayoutY() + Y_OFFSET, ISLAND_COLLISION_RADIUS*1.75);
            if (Objects.nonNull(this.prevRemovedFog))
                this.islandFog = Shape.subtract(this.islandFog, this.prevRemovedFog);
            this.islandFog.setFill(this.fogPattern);
            this.islandFog.setTranslateX(island.getLayoutX() - x/2 + X_OFFSET);
            this.islandFog.setTranslateY(island.getLayoutY() - y/2 + Y_OFFSET);
        }

        this.currentFog.setFill(this.fogPattern);
        this.prevRemovedFog = this.removedFog;
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
                    island.getPosX() + ISLAND_WIDTH/2 * ISLAND_SCALE + 17 +  distance*Math.cos(angle),
                    island.getPosY() + ISLAND_HEIGHT/2 * ISLAND_SCALE + 7 + distance*Math.sin(angle),
                    xRadius, yRadius);
            toRemoveShape = Objects.nonNull(toRemoveShape) ? Shape.union(toRemoveShape, ellipse) : ellipse;
        }

        return toRemoveShape;
    }

    public Shape getIslandFog() {
        return this.islandFog;
    }

    public Shape getCurrentFog() {
        return this.currentFog;
    }
}
