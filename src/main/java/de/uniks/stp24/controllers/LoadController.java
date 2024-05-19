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
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;

@Title("Calvario")
@Controller
public class LoadController {

    @Inject
    App app;

    @Param("autologin")
    public boolean autologin;

    @Inject
    public LoadController() {
    }

    @OnKey
    public void showLoginScreen(KeyEvent event) {
        if(autologin){
            app.show("/browseGames");
        }else {
            app.show("/login");
        }
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        if(autologin){
            app.show("/browseGames");
        }else {
            app.show("/login");
        }
    }

}
