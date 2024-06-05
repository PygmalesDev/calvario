package de.uniks.stp24.component.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "LobbyHostSettings.fxml")
public class LobbyHostSettingsComponent extends AnchorPane {
    @FXML
    public Button startJourneyButton;
    @FXML
    ImageView readyIconImageView;
    @Inject
    Subscriber subscriber;
    @Inject
    LobbyService lobbyService;
    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ImageCache imageCache;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    EventListener eventListener;
    @Inject
    @Resource
    ResourceBundle resource;

    public String gameID;
    public boolean leftLobby;
    public Image readyIconBlueImage;
    public Image readyIconGreenImage;

    @Inject
    public LobbyHostSettingsComponent() {
        this.leftLobby = false;
    }

    @OnInit
    public void init(){
        readyIconBlueImage = imageCache.get("icons/approveBlue.png");
        readyIconGreenImage = imageCache.get("icons/approveGreen.png");
    }

    public void createCheckPlayerReadinessListener() {
        this.subscriber.subscribe(this.eventListener
                .listen("games." + this.gameID + ".members.*.updated", MemberDto.class),
            result -> this.subscriber.subscribe(this.lobbyService.loadPlayers(this.gameID),
            members -> this.startJourneyButton.setDisable(!Arrays.stream(members)
                                .map(MemberDto::ready)
                                .reduce(Boolean::logicalAnd).orElse(true)),
            error -> {}));
    }

    public void setReadyButton(boolean ready){
        readyIconImageView.setImage(!ready ? readyIconBlueImage : readyIconGreenImage);
    }

    @OnRender
    public void render() {
        this.startJourneyButton.setDisable(true);
    }

    public void startGame() {
        this.app.show("/ingame");
    }

    /**
     * Sends a blank update message to the server so the members are notified about host leaving the lobby.
     */
    public void leaveLobby() {
        this.subscriber.subscribe(this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()),
          host -> this.subscriber.subscribe(this.lobbyService.updateMember(this.gameID,
                    this.tokenStorage.getUserId(), host.ready(), host.empire()),
            result -> this.app.show("/browseGames"),
            error -> {}));
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
    public void selectEmpire() {
        this.app.show("/creation", Map.of("gameid", this.gameID));
    }

    public void ready() {
        this.subscriber.subscribe(
                this.lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()),
          result -> {
                    if (result.ready()) {
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), false, result.empire()));
                        readyIconImageView.setImage(readyIconBlueImage);
                    } else {
                        this.subscriber.subscribe(this.lobbyService
                                .updateMember(this.gameID, this.tokenStorage.getUserId(), true, result.empire()));
                        readyIconImageView.setImage(readyIconGreenImage);
                    }
                },
          error -> {});
    }

    @OnDestroy
    public void destroy(){
        readyIconBlueImage = null;
        readyIconGreenImage = null;
    }
}
