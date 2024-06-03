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
        this.islandImage.setImage(imageCache.get("gameIcons/isle.jpeg"));
    }

    // an icon should be used depending on island type
    public void applyIcon(IslandType type){
        //TODO should be modified later to apply the right island types
        String pathToIcon;
        switch (type){
            case HOMELAND -> pathToIcon = "gameIcons/islands/homeland_0.png";
            case LUSHY -> pathToIcon = "gameIcons/islands/lush_0.png";
            case MISTY -> pathToIcon = "gameIcons/islands/uninhabited_0.png";
            case DESERTED -> pathToIcon = "gameIcons/islands/uninhabited_1.png";
            case BANDIT -> pathToIcon = "gameIcons/islands/bandits_0.png";
            case ANCIENT -> pathToIcon = "gameIcons/islands/uninhabited_2.png";
            case MOUNTY -> pathToIcon = "gameIcons/islands/uninhabited_3.png";
            default -> pathToIcon = "gameIcons/Isle.jpeg";
        }
        this.islandImage.setImage(imageCache.get(pathToIcon));
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
    }


}