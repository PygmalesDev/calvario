package de.uniks.stp24.component;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.LogoutService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.ResourceBundle;


@Component(view = "Logout.fxml")
public class LogoutComponent extends VBox {
    @FXML
    Button cancelButton;
    @FXML
    Button logoutButton;
    @FXML Text errorLabel;
    @FXML
    Text messageField;
    @FXML
    VBox logoutWindow;

    @Inject
    App app;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    ObjectMapper objectMapper;

    @Inject
    Subscriber subscriber;
    @Inject
    @Resource
    ResourceBundle resources;

    @Inject
    public LogoutComponent(){

    }

    @OnRender
    public void setBackground() {
        logoutWindow.setStyle("-fx-background-color: white;");
    }

    public void logout() {
        this.subscriber.subscribe(browseGameService.logout(""));
        setVisible(false);
        app.show("/login");
    }

    public void cancel() {
        setVisible(false);
    }

    public void setText(String text){
        messageField.setText(text);
    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            this.subscriber.dispose();
        }
    }
}
