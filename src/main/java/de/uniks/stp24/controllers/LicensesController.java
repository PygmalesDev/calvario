package de.uniks.stp24.controllers;


import de.uniks.stp24.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Title("Licenses")
@Controller
public class LicensesController {

    @FXML
    Button backToLoginButton;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;
    @Inject
    App app;
    @Inject
    @Resource
    ResourceBundle resources;

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
