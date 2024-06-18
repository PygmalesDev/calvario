package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

@Component(view = "IslandComponent.fxml")
@Singleton
public class IslandComponent extends Pane {
    @FXML
    public ImageView rudderImage;
    @FXML
    ImageView islandImage;
    @FXML
    public StackPane flagPane;
    @FXML
    ImageView flagImage;
    @Inject
    ImageCache imageCache;
    @Inject
    IslandAttributeStorage islandAttributes;

    private InGameController inGameController;
    public Island island;

    double x, y;
    public boolean islandIsSelected = false;


    @Inject
    public IslandComponent() {
        if (this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
    }

    public void applyIcon(IslandType type){
        this.islandImage
          .setImage(imageCache.get("icons/islands/" + type.name() + ".png"));
    }

    // use our flag images
    // by the moment numeration from 0 til 16
    public void setFlagImage(int flag){
        if (flag >=0) {
            this.flagImage
              .setImage(imageCache.get("assets/flags/flag_" + flag + ".png"));
        }
    }

    public void applyInfo(Island islandInfo) {
        this.island = islandInfo;
        applyIcon(this.island.type());
    }

    // round double to have only 2 decimals
    public void setPosition(double x, double y) {
        this.x = Math.rint(x * 100.00) / 100.00;
        this.y = Math.rint(y * 100.00) / 100.00;
    }

    public double getPosX() {
//        this.x = island.posX();
        return this.x;
    }

    public double getPosY() {
//          this.y = island.posY();
        return this.y;
    }

    // switch the visibility of all flags
    @OnKey(code = KeyCode.H, shift = true)
    public void showFlag(){
        if(island.flagIndex() >= 0){
            this.flagPane.setVisible(!flagPane.isVisible());
        }
    }


    public void showInfo() {
        System.out.println(Upgrade.values()[island.upgradeLevel()] + " -> " + island.type() + " isle at " + x + ", " + y + " -> Owner: " + island.owner());
        showFlag();
    }

    public void showRudder() {
        rudderImage.setVisible(true);

    }

    public void unshowRudder() {
        if (!islandIsSelected) {
            rudderImage.setVisible(false);
        }
    }

    public void showIslandOverview() {
        inGameController.overviewSitesComponent.resetButtons();
        if (inGameController.selectedIsland != null && inGameController.selectedIsland != this) {
            inGameController.selectedIsland.rudderImage.setVisible(false);
            inGameController.selectedIsland.islandIsSelected = false;
            if(this.island.flagIndex() >= 0) {
                this.flagPane.setVisible(!this.flagPane.isVisible());
            }
            inGameController.selectedIsland = null;
        } else if (inGameController.selectedIsland == this) {
            inGameController.overviewContainer.setVisible(false);
            inGameController.selectedIsland.rudderImage.setVisible(false);
            inGameController.selectedIsland.islandIsSelected = false;
            if(this.island.flagIndex() >= 0) {
                this.flagPane.setVisible(!this.flagPane.isVisible());
            }
            inGameController.selectedIsland = null;
            return;
        }

        islandIsSelected = true;
        showInfo();
        inGameController.showOverview(this.island);
        inGameController.selectedIsland = this;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        islandImage = null;
    }


}