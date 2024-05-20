package de.uniks.stp24.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;

import javax.inject.Inject;

@Title("Licenses")
@Controller
public class LicensesController extends BasicController {

    @FXML
    Button backToLoginButton;

    @Inject
    public LicensesController() {
    }

    public void backToLogin() {
        app.show("/login");
    }
}
