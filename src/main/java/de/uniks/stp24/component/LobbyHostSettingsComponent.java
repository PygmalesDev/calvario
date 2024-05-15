package de.uniks.stp24.component;


import de.uniks.stp24.App;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Arrays;


@Component(view = "LobbyHostSettings.fxml")
public class LobbyHostSettingsComponent extends Pane {
    @FXML
    Text gameNameField;
    @FXML
    public Button startJourneyButton;
    @Inject
    Subscriber subscriber;
    @Inject
    LobbyService lobbyService;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    EventListener eventListener;

    private String gameID;
    @Inject
    public LobbyHostSettingsComponent() {

    }

    public void createCheckPlayerReadinessListener() {
        this.subscriber.subscribe(this.eventListener
                .listen("games." + this.gameID + ".members.*.updated", MemberDto.class), result ->
                this.subscriber.subscribe(this.lobbyService.loadPlayers(this.gameID), members ->
                        this.startJourneyButton.setDisable(Arrays.stream(members)
                                .map(MemberDto::ready)
                                .reduce((a,b) -> a && b).orElse(false)))
        );
    }

    @OnRender
    public void render() {
        this.startJourneyButton.setDisable(true);
    }

    public void startGame() {
        this.app.show("/ingame");
    }

    public void leaveLobby() {
        this.subscriber.subscribe(this.gamesApiService.deleteGame(this.gameID), result ->
                this.app.show("/browsegames"));
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }
    public void selectEmpire() {
        this.app.show("/creation");
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
