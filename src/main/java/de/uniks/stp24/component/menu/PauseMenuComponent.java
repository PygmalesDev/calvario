package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.service.InGameService;
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
    App app;

    @Inject
    InGameService inGameService;

    @Inject
    @Resource
    public ResourceBundle resources;


    @Inject
    public PauseMenuComponent() {

    }

    public void resume() {
        inGameService.setPaused(false);
    }

    public void settings() {
        inGameService.setShowSettings(false);
    }

    public void quit() {
        app.show("/browseGames");
    }
}
