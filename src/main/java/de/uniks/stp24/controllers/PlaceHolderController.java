package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnKey;

import javax.inject.Inject;

@Title("place holder")
@Controller
public class PlaceHolderController {
    @Inject
    App app;
    @Inject
    public PlaceHolderController(){}
    @OnKey
    public void keyPressed(KeyEvent event) {
        System.out.println("key pressed");;
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        System.out.println("click");
    }
}
