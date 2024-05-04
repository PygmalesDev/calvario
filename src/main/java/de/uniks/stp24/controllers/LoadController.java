package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.application.Platform;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("Game Name")
@Controller
public class LoadController {

    @Inject
    App app;

    @Inject
    public LoadController() {
    }

    @OnKey
    public void showLoginScreen() {
        app.show("/login");
    }

}
