package de.uniks.stp24.component.game;

import de.uniks.stp24.component.menu.SitePropertiesComponent;
import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
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
import java.util.Arrays;
import java.util.ResourceBundle;

@Component(view = "IslandComponent.fxml")
public class IslandComponent extends Pane {
    @Inject
    ImageCache imageCache;
    @FXML
    ImageView islandImage;
    @FXML
    StackPane flagPane;
    @FXML
    ImageView flagImage;
    @Inject
    TokenStorage tokenStorage;

    @Inject
    @Resource
    ResourceBundle resource;
    private Island island;
    double x ,y ;

    InGameController inGameController;

    @Inject
    public IslandComponent(){
        if(this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
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
    public void setFlagImage(int flag){
        if (flag >=0) {
            this.flagImage
              .setImage(imageCache.get("assets/flags/flag_" + flag + ".png"));
        }
    }

    public void applyInfo(Island islandInfo){
        this.island = islandInfo;
        applyIcon(this.island.type());
    }

    // round double to have only 2 decimals
    public void setPosition(double x, double y) {
        this.x = Math.rint(x * 100.00) / 100.00;
        this.y = Math.rint(y * 100.00) / 100.00;
    }

    public double getPosX() {
        return this.x;}
    public double getPosY() {
        return this.y;}

    // switch the visibility of all flags
    @OnKey(code = KeyCode.H, shift = true)
    public void showFlag(){
        this.flagPane.setVisible(!this.flagPane.isVisible());
    }


    public void showInfo() {
        this.tokenStorage.setIsland(island);
        System.out.println("Island ID: " + tokenStorage.getIsland().id_());

        //TODO by the moment used for printouts
        // maybe it must be removed after implementation of
        // island overview functionality is completed on InGameCtrl
        System.out.println(Upgrade.values()[island.upgradeLevel()] + " -> " + island.type() + " isle at " + x + ", " + y );
        //showFlag();
    }

    public Island getIsland(){
        return this.island;
    }

    @OnDestroy
    public void destroy(){
        flagImage = null;
        islandImage = null;
    }


    public IslandComponent setTokenStorage(TokenStorage tokenStorage) {
        this.tokenStorage = tokenStorage;
        return this;
    }
}