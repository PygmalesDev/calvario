package de.uniks.stp24.component;

import de.uniks.stp24.App;
import de.uniks.stp24.service.JoinGameService;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

@Component(view = "EnterGame.fxml")
public class EnterGameComponent extends Pane {
    @FXML
    TextField passwordInputField;
    @FXML
    Text errorMessage;
    public boolean joinLobbyBoolean;
    @Inject
    App app;

    protected String gameID;

    @FXML
    Text gameNameField;
    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }

    @Inject
    public EnterGameComponent() {
        this.joinLobbyBoolean = false;
    }

    @Inject
    JoinGameService joinGameService;

    @OnRender
    public void render() {
        this.errorMessage.setText("");
    }

    public void cancel() {
        this.app.show("/browsegames");
    }

    public void joinGame() {
        if (!this.getPassword().isEmpty())
            this.joinGameService.joinGame(gameID, this.getPassword())
                .subscribe();
        else
            this.errorMessage.setText("Please enter password!");
    }

    private String getPassword() {
        return this.passwordInputField.getText();
    }
}
