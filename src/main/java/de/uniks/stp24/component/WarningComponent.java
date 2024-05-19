package de.uniks.stp24.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.EditAccService;
import de.uniks.stp24.service.TokenStorage;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
@Component(view = "Warning.fxml")
public class WarningComponent extends VBox{
    @FXML
    Text gameName;
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
    String gameNameText;

    @Inject
    public WarningComponent() {

    }

    public void setGameName(){
        gameNameText = browseGameService.getGameName();
        gameName.setText(gameNameText);
    }

    @OnRender
    public void setBackground(){
        warningWindow.setStyle("-fx-background-color: white;");
    }

    //Sets warning popup to invisible and deletes game after confirm was pressed
    public void deleteGame() {
        this.subscriber.subscribe(browseGameService.deleteGame());
        getParent().setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        if(subscriber != null) {
            this.subscriber.dispose();
        }
    }

    public void onCancel() {
        getParent().setVisible(false);
    }
}


