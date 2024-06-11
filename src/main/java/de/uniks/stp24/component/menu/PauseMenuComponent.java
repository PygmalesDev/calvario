package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnKey;

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
    TokenStorage tokenStorage;

    @Inject
    InGameService inGameService;

    @Inject
    @Resource
    public ResourceBundle resources;


    @Inject
    public PauseMenuComponent() {

    }

    public void resume() {
        if (inGameService.getPaused()) inGameService.setPaused(false);
    }

    public void settings() {
        inGameService.setShowSettings(false);
    }

    @OnKey(code = KeyCode.Q)
    public void quit() {
        tokenStorage.setGameId(null);
        tokenStorage.setEmpireId(null);
        tokenStorage.clearFlags();
        if (inGameService.getPaused()) app.show("/browseGames");
    }
}
