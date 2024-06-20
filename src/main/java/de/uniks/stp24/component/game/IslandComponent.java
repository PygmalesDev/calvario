package de.uniks.stp24.component.game;

import de.uniks.stp24.component.menu.SitePropertiesComponent;
import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.IslandAttributeStorage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Arrays;
import java.util.ResourceBundle;
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
    TokenStorage tokenStorage;

    @Inject
    @Resource
    ResourceBundle resource;
    public Island island;
 
    ImageCache imageCache;
    @Inject
    IslandAttributeStorage islandAttributes;

    private InGameController inGameController;

    double x, y;
    public boolean islandIsSelected = false;

    private boolean keyCodeH = false;


    @Inject
    public IslandComponent() {
        if (this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
        this.flagImage = new ImageView();
    }

    @OnRender
    public void render(){
        this.flagPane.setVisible(true);
    }


    public void applyIcon(IslandType type){
        this.islandImage
          .setImage(imageCache.get("icons/islands/" + type.name() + ".png"));
    }

    // use our flag images
    // by the moment numeration from 0 til 16
    public void setFlagImage(int flag) {
        if (flag >= 0) {
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
    public void showFlag(){
        if(island.flagIndex() >= 0 && !keyCodeH){
            this.flagPane.setVisible(!flagPane.isVisible());
        }
    }

    @OnKey(code = KeyCode.H, shift = true)
    public void showFlagH(){
        if(island.flagIndex() >= 0){
            this.flagPane.setVisible(!flagPane.isVisible());
        }
        keyCodeH = !keyCodeH;
    }


    public void showInfo() {
        this.tokenStorage.setIsland(island);
        System.out.println("Island ID: " + tokenStorage.getIsland().id());




        //TODO by the moment used for printouts
        // maybe it must be removed after implementation of
        // island overview functionality is completed on InGameCtrl
        System.out.println(Upgrade.values()[island.upgradeLevel()] + " -> " + island.type() + " isle at " + x + ", " + y );
        //showFlag();
    }

    public Island getIsland(){
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

    //Logic for showing rudder if other island is already selected
    public void showIslandOverview() {
        inGameController.overviewSitesComponent.resetButtons();
        if (inGameController.selectedIsland != null && inGameController.selectedIsland != this) {
            inGameController.selectedIsland.rudderImage.setVisible(false);
            if(!inGameController.selectedIsland.rudderImage.isVisible() && !keyCodeH){
                inGameController.selectedIsland.flagPane.setVisible(false);
            }
            inGameController.selectedIsland.islandIsSelected = false;
            inGameController.selectedIsland = null;
        } else if (inGameController.selectedIsland == this) {
            inGameController.overviewContainer.setVisible(false);
            inGameController.selectedIsland.rudderImage.setVisible(false);
            if(!inGameController.selectedIsland.rudderImage.isVisible() && !keyCodeH){
                inGameController.selectedIsland.flagPane.setVisible(false);
            }
            inGameController.selectedIsland.islandIsSelected = false;
            inGameController.selectedIsland = null;
            return;
        }

        islandIsSelected = true;
        if(this.island.owner() != null) {
            inGameController.showOverview(this.island);
            showFlag();
        } else {
            inGameController.overviewContainer.setVisible(false);
        }
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


    public IslandComponent setTokenStorage(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
        return this;
    }
}