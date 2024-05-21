package de.uniks.stp24.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;

@Title("Licenses")
@Controller
public class LicensesController extends BasicController {

    @FXML
    Button backToLoginButton;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;


    @Inject
    public LicensesController() {
    }

    public void backToLogin() {
        app.show("/login");
    }

    @OnDestroy
    public void destroy(){
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }
}
