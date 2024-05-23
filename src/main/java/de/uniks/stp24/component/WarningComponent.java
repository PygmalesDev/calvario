package de.uniks.stp24.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.ErrorService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;

@Component(view = "Warning.fxml")
public class WarningComponent extends VBox {
    @FXML
    Button cancelButton;
    @FXML
    Button confirmButton;
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
    ErrorService errorService;

    @Inject
    public WarningComponent() {

    }
    @OnRender
    public void setBackground() {
        warningWindow.setStyle("-fx-background-color: white;");
    }

    public void setGameName() {
        gameNameText = browseGameService.getGameName();
        gameName.setText(gameNameText);
    }

    public void deleteGame() {
        this.subscriber.subscribe(browseGameService.deleteGame(),
          result -> {},
          //TODO: WHAT SHOULD HAPPEN WITH ERROR?
          // Map it back to browse games?
          error -> errorService.getMessage(error));
          setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            this.subscriber.dispose();
        }
    }

    public void onCancel() {
        setVisible(false);
    }


}


