package de.uniks.stp24.component;

import de.uniks.stp24.service.InGameService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "Pause.fxml")
public class PauseMenuComponent extends VBox {
    @FXML
    Button resumeButton;

    @FXML
    Button settingsButton;

    @FXML
    Button quitButton;

    @FXML
    VBox vbox;

    @Inject
    InGameService inGameService;


    @Inject
    public PauseMenuComponent() {

    }

    public void resume() {
        inGameService.setPaused(false);
    }

    public void settings() {
        inGameService.setShowSettings(true);
    }

    public void quit() {
        Platform.exit();
        System.exit(0);
    }
}
