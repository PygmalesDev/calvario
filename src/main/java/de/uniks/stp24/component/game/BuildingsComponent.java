package de.uniks.stp24.component.game;

import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "buildings.fxml")
public class BuildingsComponent extends VBox {

    @Inject
    public BuildingsComponent(){

    }
}
