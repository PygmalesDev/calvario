package de.uniks.stp24.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
@Component(view = "Warning.fxml")
public class WarningComponent extends VBox{
    @FXML
    VBox warningWindow;

    @Inject
    App app;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    Subscriber subscriber;
    @Inject
    BrowseGameService browseGameService;

    @Inject
    public WarningComponent() {
    }


    @OnRender
    public void setBackground(){
        warningWindow.setStyle("-fx-background-color: grey;");
    }

    public void deleteGame() {
        browseGameService.deleteGame();
        getParent().setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }

    public void onCancel() {
        getParent().setVisible(false);
    }
}


