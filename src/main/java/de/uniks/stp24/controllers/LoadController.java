package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;

@Controller
public class LoadController {

    @Inject
    App app;

    @Inject
    public LoadController() {

    }

    @OnRender
    public void waitAndShowSignUp() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        app.show("/login");
    }

}
