package de.uniks.stp24.component.game;

import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;
import java.util.Objects;

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
        this.flagImage = new ImageView();
    }

    public void applyIcon(IslandType type) {
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
        return this.x;}
    public double getPosY() {
        return this.y;}

    // switch the visibility of all flags
//    @OnKey(code = KeyCode.H, shift = true)
    public void showFlag(boolean selected) {
        this.flagPane.setVisible(selected);
    }

    public Island getIsland() {
        return this.island;
    }

    @OnDestroy
    public void destroy() {
        flagImage = null;
        islandImage = null;
    }

}