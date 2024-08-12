package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpirePrivate;
import de.uniks.stp24.model.CircleShape;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.*;

import static de.uniks.stp24.service.Constants.*;

public class FogOfWar {
    @Inject
    public Subscriber subscriber;

    @Inject
    public EmpireApiService empireApiService;

    @Inject
    public TokenStorage tokenStorage;

    @Inject
    public IslandsService islandsService;

    InGameController inGameController;

    Image fogImage;
    ImagePattern fogPattern;

    double x, y;

    Random random = new Random();

    Shape originalFog;
    Shape currentFog;
    Shape removedFog;
    Shape prevRemovedFog;
    Shape islandFog;

    boolean night = false;

    ColorAdjust solarColorAdjust = new ColorAdjust();
    private boolean isNight = false;

    ArrayList<String> exploredIslands = new ArrayList<>();
    ArrayList<CircleShape> exploredPaths = new ArrayList<>();

    String gameID, empireID;

    @Inject
    public FogOfWar() {

    }

    public void setMapSize(double mapWidth, double mapHeight) {
        this.x = mapWidth;
        this.y = mapHeight;
    }

    public void init(InGameController inGameController) {
        this.inGameController = inGameController;
        this.gameID = tokenStorage.getGameId();
        this.empireID = tokenStorage.getEmpireId();

        this.originalFog = new Rectangle(0, 0, x, y);
        this.currentFog = originalFog;

        this.fogImage = new Image("/de/uniks/stp24/assets/backgrounds/fog.jpg");
        this.fogPattern = new ImagePattern(this.fogImage, 0, 0, this.fogImage.getWidth()*2, this.fogImage.getHeight()*2, false);

        this.solarColorAdjust = new ColorAdjust();
        this.solarColorAdjust.setBrightness(-0.75);
        this.solarColorAdjust.setContrast(-0.75);
        this.solarColorAdjust.setSaturation(-1);

        if (!tokenStorage.isSpectator()) {
            subscriber.subscribe(this.empireApiService.getPrivate(gameID, empireID),
                result -> {
                    Map<String, Object> privateMap = result._private();
                    if (Objects.nonNull(privateMap)) {
                        if (privateMap.containsKey("islandFog")) {
                            ArrayList<String> islandIDs = (ArrayList<String>) privateMap.get("islandFog");
                            this.recreateIslandFog(islandIDs);
                        }
//                        if (privateMap.containsKey("pathFog")) {
//                            ArrayList<CircleShape> circles = (ArrayList<CircleShape>) privateMap.get("pathFog");
//                            this.recreatePathFog(circles);
//                        }
                    }
                    this.updateFog(null);
                    this.inGameController.updateFog();
                },
                error -> {
                    System.out.println("Error with getting fog! " + error.getMessage());
                    this.updateFog(null);
                    this.inGameController.updateFog();
                }
        );
        }
    }

    private void recreatePathFog(ArrayList<CircleShape> circles) {
        for (CircleShape circle : circles) {
            Circle c = new Circle(circle.xPos(), circle.yPos(), 40);
            this.updateRemovedFog(c, false);
        }
    }

    private void recreateIslandFog(ArrayList<String> islandIDs) {
        for (String islandID : islandIDs)
            this.removeFogFromIsland(this.islandsService.getIslandComponent(islandID));
    }

    public void removeShapesFromFog(IslandComponent island, Shape... toRemoves) {
        this.islandFog = null;
        for (Shape shape : toRemoves) this.updateRemovedFog(shape, false);
        this.removeFogFromIsland(island);
        this.updateFog(island);
    }

    private void removeFogFromIsland(IslandComponent island) {
        if (Objects.nonNull(island)) {
            this.exploredIslands.add(island.island.id());
            island.applyIcon(false, night?BlendMode.MULTIPLY:BlendMode.LIGHTEN);
            island.applyEmpireInfo();
            this.updateRemovedFog(new Circle(island.getPosX() + X_OFFSET, island.getPosY() + Y_OFFSET, ISLAND_COLLISION_RADIUS), true);
            this.updateRemovedFog(this.randomFogAroundIsland(island), true);
        }
    }

    private void updateRemovedFog(Shape shape, boolean isIsland) {
        if (Objects.nonNull(shape)) {
            if (!isIsland && shape instanceof Circle circle)
                this.exploredPaths.add(new CircleShape(circle.getCenterX(), circle.getCenterY()));
            this.removedFog = Objects.nonNull(this.removedFog) ? Shape.union(this.removedFog, shape) : shape;
        }

    }

    private void updateFog(IslandComponent island) {
        if (Objects.nonNull(this.removedFog))
            this.currentFog = Shape.subtract(this.originalFog, this.removedFog);

        if (Objects.nonNull(island)) {
            this.islandFog = new Circle(island.getLayoutX() + FOG_X_OFFSET, island.getLayoutY() + FOG_Y_OFFSET, ISLAND_COLLISION_RADIUS*1.75);
            if (Objects.nonNull(this.prevRemovedFog))
                this.islandFog = Shape.subtract(this.islandFog, this.prevRemovedFog);
            this.islandFog.setFill(this.fogPattern);
            if (isNight) this.islandFog.setEffect(solarColorAdjust);
            this.islandFog.setTranslateX(island.getLayoutX() - x/2 + FOG_X_OFFSET);
            this.islandFog.setTranslateY(island.getLayoutY() - y/2 + FOG_Y_OFFSET);
        }

        this.currentFog.setFill(this.fogPattern);
        if (isNight) this.currentFog.setEffect(solarColorAdjust);
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

    public void saveFog() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID, this.empireID),
                result -> {
                    final Map<String, Object> newPrivateMap = Objects.nonNull(result._private()) ?
                                    result._private() : new HashMap<>();
                    newPrivateMap.put("islandFog", this.exploredIslands);
//                    newPrivateMap.put("pathFog", this.exploredPaths);
                    subscriber.subscribe(this.empireApiService.savePrivate(this.gameID, this.empireID, new EmpirePrivate(newPrivateMap)),
                            saved -> {},
                            error -> System.out.println("error while saving fog: " + error.getMessage()));
                },
                error -> System.out.println("error while getting fog: " + error.getMessage())
        );
    }

    public void setIsNight(boolean isNight) {
        this.isNight = isNight;
    }
}
