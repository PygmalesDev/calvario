package de.uniks.stp24.controllers;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.param.Param;
import javax.inject.Inject;
import java.util.ResourceBundle;

@Title("Calvario")
@Controller
public class LoadController extends BasicController{
    @FXML
    HBox backgroundHBox;
    @FXML
    Image calvarioLogoLoad;
    @FXML
    Image spiderbyteLogoLoad;
    @FXML
    Image deadBirdsLogo;


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

    @OnDestroy
    public void destroy(){
        backgroundHBox = null;
        deadBirdsLogo = null;
        calvarioLogoLoad = null;
        spiderbyteLogoLoad = null;
    }

}
