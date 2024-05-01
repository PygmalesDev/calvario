package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import org.fulib.fx.annotation.controller.Controller;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;

@Controller
public class LoadController {

    @Inject
    App app;

    @Inject
    public LoadController() {
        waitAndShowSignUp();
    }

    public void waitAndShowSignUp() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        app.show("/login");
    }

}
