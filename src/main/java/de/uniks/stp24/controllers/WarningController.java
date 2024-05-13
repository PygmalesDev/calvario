package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import javax.inject.Inject;

public class WarningController {
    @FXML
    Button confirmButton;
    @FXML
    Button cancelButton;
    @FXML
    Text gameName;
    @Inject
    App app;

    @Inject
    public WarningController() {
    }

    public void deleteGame() {
    }

    public void onCancel() {
    }
}
