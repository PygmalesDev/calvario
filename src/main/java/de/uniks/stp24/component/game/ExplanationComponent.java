package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;
import java.io.IOException;

@Component(view = "ExplanationComponent.fxml")
public class ExplanationComponent extends VBox {

    @FXML
    public Text inf;

    @Inject
    public ExplanationComponent(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ExplanationComponent.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInf(String multiplierInf){
        inf.setText(multiplierInf);
    }
}
