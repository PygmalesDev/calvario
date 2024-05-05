package de.uniks.stp24.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Controller
public class PauseController {
    @FXML
    VBox vbox;
    @FXML
    Button resumeButton;
    @FXML
    Button settingsButton;
    @FXML
    Button quitButton;

    @Inject
    public PauseController() {

    }

    @OnRender
    public void render() {
        setVisible(true);
    }

    public void setVisible(Boolean visible) {
        vbox.setVisible(visible);
    }

    public VBox getVbox() {
       return vbox;
    }

    public void resume(ActionEvent actionEvent) {

    }

    public void settings(ActionEvent actionEvent) {

    }

    public void quit(ActionEvent actionEvent) {

    }
}
