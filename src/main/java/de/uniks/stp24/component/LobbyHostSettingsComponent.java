package de.uniks.stp24.component;


import de.uniks.stp24.App;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.LobbyService;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;


@Component(view = "LobbyHostSettings.fxml")
public class LobbyHostSettingsComponent extends Pane {
    @FXML
    Text gameNameField;
    @Inject
    LobbyService lobbyService;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    App app;
    private String gameID;

    @Inject
    public LobbyHostSettingsComponent() {

    }

    public void selectEmpire() {
        System.out.println("Select Empire");
    }

    public void ready() {
        System.out.println("Ready");
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void startGame() {
        // TODO: Check if all players are ready
        System.out.println("Game started");
    }

    public void leaveLobby() {
        this.gamesApiService.deleteGame(this.gameID).subscribe(result ->
                this.app.show("/browsegames"));
    }

    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }
}
