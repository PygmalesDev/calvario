package de.uniks.stp24.controllers;


import de.uniks.stp24.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;

@Title("Licenses")
@Controller
public class LicensesController {

    @FXML
    Button backToLoginButton;
    @Inject
    App app;

    @Inject
    public LicensesController() {
    }

    public void backToLogin() {
        app.show("/login");
    }
}
