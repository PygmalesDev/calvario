package de.uniks.stp24.component.game;

import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;

import javax.inject.Inject;


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
    private Island island;
    double x ,y ;

    @Inject
    public IslandComponent(){
        if(this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
    }

    public void applyIcon(IslandType type){
        this.islandImage
          .setImage(imageCache.get("icons/islands/" + type.name() + ".png"));
    }

    // todo flag images
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

    // set double to have only 2 decimals
    public void setPosition(double x, double y) {
        this.x = Math.rint(x * 100.00) / 100.00;
        this.y = Math.rint(y * 100.00) / 100.00;
//        return this;
    }

    public double getPosX() {
//        this.x = island.posX();
        return this.x;}
    public double getPosY() {
//          this.y = island.posY();
        return this.y;}

    // switch the visibility of all flags
    @OnKey(code = KeyCode.A, shift = true)
    public void showFlag(){
        this.flagPane.setVisible(!this.flagPane.isVisible());
    }

    // by the moment change visibility of flag (image)
    public void showInfo() {
        //TODO show info pane!
        System.out.println(Upgrade.values()[island.upgradeLevel()] + " -> " + island.type() + " isle at " + x + ", " + y );
        this.flagPane.setVisible(!this.flagPane.isVisible());
    }

    @OnDestroy
    public void destroy(){
        flagImage = null;
        islandImage = null;
    }


}