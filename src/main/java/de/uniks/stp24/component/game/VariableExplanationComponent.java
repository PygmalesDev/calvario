package de.uniks.stp24.component.game;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import java.io.IOException;

@Component(view = "VariableExplanationOverview.fxml")
public class VariableExplanationComponent extends VBox {

    public VariableExplanationComponent(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VariableExplanationOverview.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /*

    Komponente zur Anzeige der Informationen.

    Methode zum setten der einezelenen Werte.
    Hole Informationen aus dem Storage.

     */
}
