package de.uniks.stp24.component;


import de.uniks.stp24.App;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;


@Component(view = "LobbySettings.fxml")
public class LobbySettingsComponent extends Pane {
    @FXML
    Text gameNameField;
    @Inject
    Subscriber subscriber;
    @Inject
    LobbyService lobbyService;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;

    private String gameID;
    public boolean leftLobby = false;

    @Inject
    public LobbySettingsComponent() {

    }

    public void leaveLobby() {
        this.leftLobby = true;
        this.lobbyService.leaveLobby(this.gameID, this.tokenStorage.getUserId()).subscribe(result ->
                this.app.show("/browseGames"));
    }

    public void selectEmpire() {
        this.app.show("/creation");
    }
    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void ready() {
        this.subscriber.subscribe(
                this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()), result -> {
                    if (result.ready())
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), false, result.empire()));
                    else
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), true, result.empire()));
                });
    }

}
