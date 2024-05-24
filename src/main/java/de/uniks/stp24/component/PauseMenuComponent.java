package de.uniks.stp24.component;

import de.uniks.stp24.service.InGameService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "Pause.fxml")
public class PauseMenuComponent extends AnchorPane {
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
    @Resource
    ResourceBundle resource;


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
