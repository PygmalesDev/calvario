package de.uniks.stp24.component;


import de.uniks.stp24.App;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.LobbyService;
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
    Button startJourneyButton;
    @Inject
    LobbyService lobbyService;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;
    private String gameID;

    @Inject
    public LobbyHostSettingsComponent() {

    }

    @OnRender
    public void render() {
        this.startJourneyButton.setDisable(true);
    }

    @OnRender
    public void checkPlayerReadiness() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + this.gameID + ".members.*.updated", MemberDto.class), result ->
            this.subscriber.subscribe(this.lobbyService.loadPlayers(this.gameID), members ->
                    this.startJourneyButton.setDisable(!Arrays.stream(members)
                            .map(MemberDto::ready)
                            .reduce((a,b) -> a && b).orElse(false)))
        );
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
        this.lobbyService.loadPlayers(this.gameID).subscribe(result -> {
                    boolean ready = Arrays.stream(result)
                            .map(MemberDto::ready)
                            .reduce((a, b) -> a && b).orElse(false);
                    if (ready)
                        this.app.show("/ingame");
                }
        );
    }

    public void leaveLobby() {
        this.gamesApiService.deleteGame(this.gameID).subscribe(result ->
                this.app.show("/browsegames"));
    }

    public void setGameName(String gameName) {
        this.gameNameField.setText(gameName);
    }
}
