package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.LogoutService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;
import java.util.Map;

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
//    @Inject
//    Subscriber subscriber;
    @Param("info")
    public String info;

    @Inject
    public LogoutController(){

    }

    public void logout(ActionEvent actionEvent) {

        logoutService.logout("")
                .doFinally(() -> {
                    System.out.println("LOGGING OUT");
                    info = "logout successful";
                })
                .subscribe().dispose();
        app.show("/login", Map.of("info",info));
    }

    public void cancel(ActionEvent actionEvent) {
        app.show("/browseGames");
    }
    @OnDestroy
    public void destroy() {

    }
}