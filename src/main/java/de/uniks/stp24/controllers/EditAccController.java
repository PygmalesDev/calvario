package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

@Title("Edit Account")
@Controller
public class EditAccController {

    @FXML
    TextField passwordInput;
    @FXML
    TextField usernameInput;

    @Inject
    App app;

    @Inject
    public EditAccController() {
    }

    public void saveChanges(ActionEvent actionEvent) {

    }

    public void cancelChanges(ActionEvent actionEvent) {
    }

    public void continueGame(ActionEvent actionEvent) {
    }

    public void editAcc(ActionEvent actionEvent) {

    }

    public void deleteUser(ActionEvent actionEvent) {
    }

    public void goBack(ActionEvent actionEvent) {
        app.show("/browseGames");
    }
}
