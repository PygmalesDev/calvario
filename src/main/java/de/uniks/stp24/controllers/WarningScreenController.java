package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fulib.fx.annotation.controller.Controller;

import javax.inject.Inject;

@Controller
public class WarningScreenController {

    @FXML
    VBox warningContainer;

    @Inject
    App app;

    @Inject
    public WarningScreenController() {}


    public void cancelDelete(ActionEvent actionEvent) {
    }

    public void deleteAcc(ActionEvent actionEvent) {
    }
}
