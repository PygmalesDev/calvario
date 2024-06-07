package de.uniks.stp24.component.menu;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.menu.BrowseGameService;
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
    Text warningText;
    @FXML
    Button cancelButton;
    @FXML
    Button logoutButton;
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
    public void render() {
        logoutWindow.setStyle("-fx-background-color: white;");
        warningText.setText(resources.getString("you.will.be.logged.out"));
    }

    public void logout() {
        this.subscriber.subscribe(browseGameService.logout(""));
        this.getParent().setVisible(false);
        app.show("/login");
    }

    public void cancel() {
        this.getParent().setVisible(false);
    }

    public void setText(){

    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            this.subscriber.dispose();
        }
    }
}
