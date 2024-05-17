package de.uniks.stp24.controllers;


import de.uniks.stp24.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Title("Licenses")
@Controller
public class LicensesController {

    @FXML
    Button backToLoginButton;
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
}
