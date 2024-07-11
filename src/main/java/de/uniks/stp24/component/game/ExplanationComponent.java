package de.uniks.stp24.component.game;

import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "ExplanationComponent.fxml")
public class ExplanationComponent extends VBox {

    @Inject
    public ExplanationComponent(){
    }
}
