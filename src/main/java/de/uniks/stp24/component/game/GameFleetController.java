package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.helper.DistancePoint;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.Constants.POINT_TYPE;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.FleetService;
import javafx.animation.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static de.uniks.stp24.service.Constants.FLEET_HW;

@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle collisionCircle;
    public Circle empireCircle;
    public ImageView fleetImage;

    private final DropShadow selectedDropShadow;
    private final FleetService fleetService;
    private final TokenStorage tokenStorage;
    private final ImageCache imageCache;
    private final FleetCoordinationService fleetCoordinationService;
    private DistancePoint currentPoint;
    private final Timeline travelTimeline = new Timeline();
    private final Rotate rotate = new Rotate();
    private Fleet fleet;

    @Inject
    public GameFleetController(Fleet fleet, FleetCoordinationService fleetCoordinationService){
        this.fleet = fleet;
        this.fleetService = fleetCoordinationService.fleetService;
        this.imageCache = fleetCoordinationService.imageCache;
        this.fleetCoordinationService = fleetCoordinationService;
        this.tokenStorage = fleetCoordinationService.tokenStorage;

        this.selectedDropShadow = new DropShadow();
        this.selectedDropShadow.setHeight(20);
        this.selectedDropShadow.setWidth(20);
        this.selectedDropShadow.setSpread(0.9);

        this.setId("ingameFleet_" + fleet._id());

        travelTimeline.currentTimeProperty().addListener(this::listnerMethod);

        this.travelTimeline.setOnFinished(event -> {
            if (this.currentPoint.getType().equals(POINT_TYPE.ISLAND)){
                System.out.println( this.currentPoint.islandComponent);
                System.out.println(this.currentPoint);
                this.travelTimeline.currentTimeProperty().removeListener(this::listnerMethod);
                this.fleetCoordinationService.inGameController.removeFogFromIsland(true, this.currentPoint.islandComponent);
                travelTimeline.currentTimeProperty().addListener(this::listnerMethod);
            }
        });
    }

    private void listnerMethod(ObservableValue<? extends Duration> observableValue, Duration duration, Duration duration1) {
        fleetCoordinationService.inGameController.removeFogFromShape(new Circle(this.getLayoutX() + FLEET_HW/2 + 10,
                this.getLayoutY() + FLEET_HW/2 + 15,
                collisionCircle.getRadius()*1.3)
        );
    }

    public void renderWithColor(String color) {
        this.empireCircle.setStroke(Color.web(color));
        this.selectedDropShadow.setColor(Color.web(color));

        this.fleetImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/fleet_on_map.png"));
        this.collisionCircle.setPickOnBounds(true);
    }

    public void select() {
        if (Objects.nonNull(this.fleet.empire()) && this.fleet.empire().equals(this.tokenStorage.getEmpireId()))
            this.fleetCoordinationService.setFleet(this);
    }

    public void toggleActive() {
        if (Objects.isNull(this.fleetImage.getEffect())) this.fleetImage.setEffect(this.selectedDropShadow);
        else this.fleetImage.setEffect(null);
    }

    public void travelToPoint(List<KeyFrame> keyFrame, DistancePoint currentPoint) {
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();
        this.currentPoint = currentPoint;

        this.travelTimeline.getKeyFrames().addAll(keyFrame);
        this.travelTimeline.play();
    }

    public void stopTravel() {
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();
    }

    public DistancePoint getCurrentPoint() {
        return this.currentPoint;
    }

    public DistancePoint getCurrentLocation() {
        return new DistancePoint(
                this.getLayoutX()  + FLEET_HW,
                this.getLayoutY() + FLEET_HW,
                POINT_TYPE.FLEET,
                null
        );
    }

    public boolean isCollided(Circle other) {
        return this.collisionCircle.intersects(other.getLayoutBounds());
    }

    public Fleet getFleet() {
        return fleet;
    }

    public void setStartingPoint() {
        this.fleetImage.getTransforms().add(rotate);
    }

    public Rotate getFleetRotate() {
        return this.rotate;
    }

    public void setCurrentPoint(DistancePoint currentPoint) {
        this.currentPoint = currentPoint;
    }

    public void setFleet(Fleet fleet) {
        this.fleet = fleet;
    }
}
