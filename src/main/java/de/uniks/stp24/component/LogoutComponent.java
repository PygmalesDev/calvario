package de.uniks.stp24.component;


import de.uniks.stp24.service.LogoutService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;


@Component(view = "Logout.fxml")
public class LogoutComponent extends VBox {
    @FXML
    Button cancelButton;
    @FXML
    Button logoutButton;
    @FXML Text errorLabel;
    @FXML
    Text messageField;
    @FXML
    VBox logoutWindow;

    @Inject
    LogoutService logoutService;

    public void logout() {
    }

    public void cancel() {
    }

    public void setText(String text){
        messageField.setText(text);
    }
}
