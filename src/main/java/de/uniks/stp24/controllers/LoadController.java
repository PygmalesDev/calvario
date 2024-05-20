package de.uniks.stp24.controllers;

import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnKey;

import javax.inject.Inject;

@Title("%load")
@Controller
public class LoadController extends BasicController{

    @Inject
    public LoadController() {
    }

    @OnKey
    public void showLoginScreen() {
        app.show("/login");
    }

    public void mouseClicked() {
        app.show("/login");
    }

}
