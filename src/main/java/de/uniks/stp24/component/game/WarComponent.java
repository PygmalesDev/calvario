package de.uniks.stp24.component.game;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "War.fxml")
public class WarComponent extends AnchorPane {
    @FXML
    Button closeButton;
    @FXML
    Text warText;

    @Inject
    public WarComponent() {

    }
}
