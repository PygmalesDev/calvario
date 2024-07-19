package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "Pause.fxml")
public class PauseMenuComponent extends AnchorPane {
    public ImageView marketInfoImage;
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
    ImageCache imageCache;

    @Inject
    InGameService inGameService;

    @Inject
    @Resource
    public ResourceBundle resources;
    private InGameController inGameController;


    @Inject
    public PauseMenuComponent() {

    }

    @OnRender
    public void render() {
        marketInfoImage.setImage(imageCache.get("icons/information-button2.png"));

    }

    public void resume() {
        if (inGameService.getPaused()) inGameService.setPaused(false);
    }

    public void help() {
        inGameController.showHelp();
    }

    @OnKey(code = KeyCode.Q)
    public void quit() {
        tokenStorage.setGameId(null);
        tokenStorage.setEmpireId(null);
        if (inGameService.getPaused()) app.show("/browseGames");
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}