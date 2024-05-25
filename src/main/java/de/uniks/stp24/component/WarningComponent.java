package de.uniks.stp24.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.ErrorService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;

import javax.inject.Inject;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
    @Inject
    @Resource
    ResourceBundle resources;

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

    StackPane changableView;
    public void setView(StackPane view) {
        this.changableView = view;
    }

    public void deleteGame() {
        this.subscriber.subscribe(browseGameService.deleteGame(),
          result -> changableView.setVisible(false),
                //TODO: WHAT SHOULD HAPPEN WITH ERROR?
                // Map it back to browse games?
                // nothing :)
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
        this.getParent().setVisible(false);
    }


}


