package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("AutoLogin")
@Controller
public class AutoLoginController {

    @Inject
    App app;

    @Inject
    public AutoLoginController() {
    }

    @OnKey()
    public void keyPressed(KeyEvent event) {
        // app.show("/browseGames");
        app.show("/holder");
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        app.show("/holder");
    }
}
