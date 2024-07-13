package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "ExplanationComponent.fxml")
public class ExplanationComponent extends VBox {

    @FXML
    public Text inf;

    @Inject
    public ExplanationComponent(){
    }

    public void setInf(String multiplierInf){
        inf.setText(multiplierInf);
    }
}
