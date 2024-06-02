package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "IslandComponent.fxml")
public class IslandComponent extends ImageView {
    @Inject
    ImageCache imageCache;
    @FXML
    ImageView islandImage;
    private Island island;
    int x ,y ;

    @Inject
    public IslandComponent(){
        if(this.imageCache == null) {
            this.imageCache = new ImageCache();
        }
        this.islandImage = new ImageView();
        this.islandImage.setImage(imageCache.get("gameIcons/isle.jpeg"));
    }

    // an icon should be used depending on island type
    public IslandComponent applyIcon(IslandType type){
        String path;
        switch (type){
            case HOMELAND -> path = "gameIcons/Isle.jpeg";
            case LUSHY -> path = "gameIcons/Isle.jpeg";
            case MISTY -> path = "gameIcons/Isle.jpeg";
            case DESERTED -> path = "gameIcons/Isle.jpeg";
            case BANDIT -> path = "gameIcons/Isle.jpeg";
            case ANCIENT -> path = "gameIcons/Isle.jpeg";
            case MOUNTY -> path = "test/911.png";
            default -> path = "gameIcons/Isle.jpeg";
        }
        this.islandImage.setImage(imageCache.get(path));
        return this;
    }

    public void applyInfo(Island islandInfo){
        this.island = islandInfo;
        applyIcon(this.island.type());
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getPosX() {
//        this.x = island.posX();
        return this.x;}
    public int getPosY() {
//          this.y = island.posY();
        return this.y;}


    public void showInfo() {
        //TODO show info
        System.out.println(x + ", " + y);
    }
}