package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Controller
public class InGameController {
    @FXML
    Pane pane;
    @Inject
    App app;

    @Inject
    PauseController pauseController;

    @Inject
    public InGameController() {

    }

    @OnRender
    public void init() {

    }

    @OnKey(code = KeyCode.ESCAPE)
    public void pauseGame() {
        // TODO make pause to component
        app.show("/pause");
        // pane.getChildren().add(pauseController.getVbox());
    }
}
