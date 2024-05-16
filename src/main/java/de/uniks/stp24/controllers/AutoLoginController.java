package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnKey;

import javax.inject.Inject;

@Title("AutoLogin")
@Controller
public class AutoLoginController {

    @Inject
    App app;

    @Inject
    public AutoLoginController() {
    }

    // show browse games screen after any click
    @OnKey() // route("/browsegame")
    public void keyPressed() {
        app.show("/browseGames");
    }

    public void mouseClicked() {
        app.show("/browseGames");
    }
}
