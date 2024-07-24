package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ResourceBundle;


@Component(view = "IslandComponent.fxml")
@Singleton
public class IslandComponent extends Pane {
    @FXML
    public ImageView rudderImage;
    @FXML
    public ImageView islandImage;
    @FXML
    public StackPane flagPane;
    public Circle collisionCircle;
    @FXML
    ImageView flagImage;
    @FXML
    ImageView spyglassImage;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    @Resource
    ResourceBundle resource;
    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    FleetService fleetService;

    IslandsService islandsService;

    ImageCache imageCache;

    public Island island;

    public InGameController inGameController;

    double x, y;

    public boolean islandIsSelected = false;

    @Inject
    public IslandComponent() {
        if (this.imageCache == null) this.imageCache = new ImageCache();
        this.islandImage = new ImageView();
        this.flagImage = new ImageView();
        this.spyglassImage = new ImageView();
        this.setPickOnBounds(false);
    }

    public void applyIcon(IslandType type) {
        this.islandImage.setImage(imageCache.get("icons/islands/" + type.name() + ".png"));

        if (this.island.upgrade().equals("explored"))
            this.spyglassImage.setImage(imageCache.get("/de/uniks/stp24/icons/other/spyglass.png"));
        else // islands with upgrades other than explored
            hideSpyGlass();
    }

    // use our flag images
    // by the moment numeration from 0 til 16
    public void setFlagImage(int flag) {
        if (flag >= 0) this.flagImage.setImage(imageCache.get("assets/flags/flag_" + flag + ".png"));
    }

    public void applyInfo(Island islandInfo) {
        this.island = islandInfo;
        this.setId(island.id() + "_instance");
        this.spyglassImage.setVisible(island.upgrade().equals("explored"));
        applyIcon(this.island.type());
    }

    // round double to have only 2 decimals
    public void setPosition(double x, double y) {
        this.x = Math.rint(x * 100.00) / 100.00;
        this.y = Math.rint(y * 100.00) / 100.00;
    }

    public double getPosX() {
        return this.x;
    }

    public double getPosY() {
        return this.y;
    }

    // switch the visibility of all flags
    public void showFlag(boolean selected) {
        this.flagPane.setVisible(selected);
        inGameController.islandsService.keyCodeFlag = selected;
    }

    @OnKey(code = KeyCode.F, alt = true)
    public void showFlagH() {
        if (island.flagIndex() >= 0 && !islandIsSelected) {
            this.flagPane.setVisible(!flagPane.isVisible());
        }
    }

    public Island getIsland() {
        return this.island;
    }

    public void showRudder() {
        rudderImage.setVisible(true);
    }

    public void unshowRudder() {
        if (!islandIsSelected) {
            rudderImage.setVisible(false);
        }
    }

    /*
    Logic for showing/unshowing rudder
     */
    public void showUnshowRudder() {
        if (islandIsSelected) {
            reset();
            islandIsSelected = false;
        } else {
            if (island.owner() != null) {
                inGameController.islandsService.islandComponentMap.forEach((id, comp) -> {
                    if (comp.islandIsSelected) {
                        comp.rudderImage.setVisible(false);
                        if (!inGameController.islandsService.keyCodeFlag) {
                            comp.flagPane.setVisible(!comp.flagPane.isVisible());
                        }
                        comp.islandIsSelected = false;
                    }
                });
                islandIsSelected = true;
            }
        }

        if (!inGameController.islandsService.keyCodeFlag) {
            this.flagPane.setVisible(!this.flagPane.isVisible());
        }
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        islandImage = null;
    }


    public IslandComponent setTokenStorage(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
        return this;
    }

    /*
    Reset of componentes for showing informations of current selected island.
     */

    public void reset() {
        inGameController.overviewSitesComponent.resetButtons();
        inGameController.buildingsWindowComponent.setVisible(false);
        inGameController.sitePropertiesComponent.setVisible(false);
        inGameController.buildingPropertiesComponent.setVisible(false);
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;

        if (!inGameController.islandsService.keyCodeFlag) {
            inGameController.selectedIsland.rudderImage.setVisible(false);
        }

        inGameController.selectedIsland = null;
    }

    public void applyEmpireInfo() {
        // apply drop shadow and flag for newly colonized systems
        this.islandsService.applyDropShadowToIsland(this);
        this.setFlagImage(islandsService.getEmpire(island.owner()).flag());
    }

    public void setIslandService(IslandsService islandsService) {
        this.islandsService = islandsService;
    }

    public void hideSpyGlass() {
        this.spyglassImage.setVisible(false);
    }

    public boolean isCollided(double fleetX, double fleetY, double fleetR) {
        double x1 = this.getPosX();
        double y1 = this.getPosY();
        double r1 = Constants.ISLAND_COLLISION_RADIUS;
//        System.out.println("island: " + "x: " +x1 +" y: " + y1 + " r: " +r1);

        double x2 = fleetX;
        double y2 = fleetY;
        double r2 = fleetR;
//        System.out.println("fleet: " + "x: " +x2 +" y: " + y2 + " r: " +r2);

        boolean distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) <= r1 + r2;

        return distance;
    }

}