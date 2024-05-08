package de.uniks.stp24.component;

import de.uniks.stp24.service.JoinGameService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Component(view = "EnterGame.fxml")
public class EnterGameComponent extends Pane {
    @FXML
    TextField passwordInputField;
    @FXML
    Text gameNameField;
    @FXML
    Text errorMessage;
    public boolean joinLobbyBoolean;

    @Inject
    public EnterGameComponent() {
        this.joinLobbyBoolean = false;
    }

    private String gameID;

    @Inject
    JoinGameService joinGameService;

    @OnRender
    public void render() {
        this.errorMessage.setText("");
    }

    public void cancel() {
        System.out.println("Canceled");
    }

    public void joinGame() {
        this.joinGameService.joinGame(gameID, this.getPassword())
                .subscribe();
    }

    private String getPassword() {
        return this.passwordInputField.getText();
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }
}
