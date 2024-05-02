package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.application.Platform;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("Game Name")
@Controller
public class LoadController {

    @Inject
    App app;

    public class ThreadRunner implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                Platform.runLater(LoadController.this::showLoginScreen);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Inject
    public LoadController() {

    }

    @OnRender
    public void threadHandler() {
        Thread t = new Thread(new ThreadRunner());
        t.start();

    }

    public void showLoginScreen() {
        app.show("/login");
    }

}
