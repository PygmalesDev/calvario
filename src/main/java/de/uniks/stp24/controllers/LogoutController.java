package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.service.LogoutService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;

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
    public LogoutController(){

    }

    public void logout(ActionEvent actionEvent) {
        logoutService.logout("")
                .subscribe(res -> {
                            System.out.println("LOGGING OUT");
                        }
                );
        app.show("/login");
    }

    public void cancel(ActionEvent actionEvent) {
        app.show("/browseGames");
    }
}