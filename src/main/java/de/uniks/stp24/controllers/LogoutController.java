package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.LogoutService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;

@Title("Logout")
@Controller
public class LogoutController{

    @FXML
    Button logoutButton;
    @FXML
    Button cancelButton;
    @FXML
    Text messageField;
    @FXML
    Text errorLabel;
    @Inject
    App app;
    @Inject
    LogoutService logoutService;
    @Inject
    @Resource
    ResourceBundle resources;
    @Param("info")
    public String info;

    @Inject
    public LogoutController() {

    }

    public void logout() {

        logoutService.logout("")
                .doFinally(() -> {
                    System.out.println("LOGGING OUT");
                    info = resources.getString("logout.successful.on.this.device");
                })
                .subscribe().dispose();
        app.show("/login", Map.of("info",info));

    }

    public void cancel() {
        app.show("/browseGames");
    }
    @OnDestroy
    public void destroy() {

    }
}