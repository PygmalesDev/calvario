package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.helper.DistancePoint;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.service.Constants.POINT_TYPE;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetCoordinationService;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
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
import org.fulib.fx.annotation.event.OnDestroy;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

import static de.uniks.stp24.service.Constants.FLEET_HW;


@Component(view = "GameFleet.fxml")
public class GameFleetController extends Pane {
    public Circle collisionCircle;

    @FXML
    public ProgressBar healthBar;

    private double fleetHealth;
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
    private boolean ownFleet;
    public Fleet fleet;

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

        this.travelTimeline.currentTimeProperty().addListener(this::listenerTimeMethod);

        this.travelTimeline.statusProperty().addListener(this::listenerStatusMethod);
    }

    private void listenerStatusMethod(ObservableValue<? extends Animation.Status> observableValue, Animation.Status status, Animation.Status status1) {
        if (status1.equals(Animation.Status.STOPPED) && this.currentPoint.getType().equals(POINT_TYPE.ISLAND) &&
        this.ownFleet)
            this.fleetCoordinationService.inGameController.removeFogFromIsland(true, this.currentPoint.islandComponent);
    }

    private void listenerTimeMethod(ObservableValue<? extends Duration> observableValue, Duration duration, Duration duration1) {
        if (this.ownFleet) {
            fleetCoordinationService.inGameController.removeFogFromShape(new Circle(this.getLayoutX() + FLEET_HW / 2 + 10,
                    this.getLayoutY() + FLEET_HW / 2 + 15,
                    collisionCircle.getRadius() * 1.3)
            );
        }
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

    public void travelToPoint(List<KeyFrame> keyFrame, DistancePoint currentPoint, boolean ownFleet) {
        this.travelTimeline.stop();
        this.travelTimeline.getKeyFrames().clear();
        this.currentPoint = currentPoint;
        this.ownFleet = ownFleet;
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

    public Fleet getFleet() {
        return this.fleet;
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

    @OnDestroy
    public void onDestroy(){
        this.travelTimeline.statusProperty().removeListener(this::listenerStatusMethod);
        this.travelTimeline.currentTimeProperty().removeListener(this::listenerTimeMethod);
        this.travelTimeline.stop();
    }
}
