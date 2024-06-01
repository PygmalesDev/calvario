package de.uniks.stp24.component.game;


import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component
public class IslandComponent extends AnchorPane {
    @FXML
    ImageView islandImage;
    @Inject
    public IslandComponent(){}
}
