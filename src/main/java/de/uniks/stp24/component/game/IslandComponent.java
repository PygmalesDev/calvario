package de.uniks.stp24.component.game;

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

import static java.lang.Thread.sleep;

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
    final String[] flags = {
      "847.png", "863.png", "911.png", "927.png", "959.png"
    };

    @Inject
    public IslandComponent(){
        if(this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
        this.islandImage.setImage(imageCache.get("icons/isle.jpeg"));
    }

    // an icon should be used depending on island type
    public void applyIcon(IslandType type){
        //TODO should be modified later to apply the right island types
        String pathToIcon = "icons/islands/";
        switch (type){
            case HOMELAND -> pathToIcon += "regular.png";
            case LUSHY -> pathToIcon += "uninhabitable_0.png";
            case MISTY -> pathToIcon += "golden_0.png";
            case DESERTED -> pathToIcon += "uninhabitable_1.png";
            case BANDIT -> pathToIcon += "ancient_military.png";
            case ANCIENT -> pathToIcon += "uninhabitable_2.png";
            case MOUNTY -> pathToIcon += "uninhabitable_3.png";
            default -> pathToIcon = "icons/Isle.jpeg";
        }
        this.islandImage.setImage(imageCache.get(pathToIcon));
    }

    public void setFlagImage(int flag){
        this.flagImage
          .setImage(imageCache.get("test/" + flags[flag]));
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


    public void showInfo() {
        //TODO show info pane!
        System.out.println("Isle at " + x + ", " + y);
        this.flagPane.setVisible(!this.flagPane.isVisible());
    }

    @OnDestroy
    public void destroy(){
        flagImage = null;
        islandImage = null;
    }


}