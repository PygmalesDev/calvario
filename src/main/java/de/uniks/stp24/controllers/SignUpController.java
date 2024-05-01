package de.uniks.stp24.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.fulib.fx.annotation.controller.Controller;

@Controller
public class SignUpController {
    @FXML
    Button registerButton;
    @FXML
    TextField usernameField;
    @FXML
    TextField passwordField;

    public SignUpController() {
        registerButton.setOnMousePressed(this::register);
    }

    private void register(MouseEvent mouseEvent) {
    }


}
