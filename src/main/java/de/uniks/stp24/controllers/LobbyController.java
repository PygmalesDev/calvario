package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.EnterGameComponent;
import de.uniks.stp24.component.LobbyHostSettingsComponent;
import de.uniks.stp24.component.LobbySettingsComponent;
import de.uniks.stp24.component.UserComponent;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.GamesService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;

@Title("Enter Game")
@Controller
public class LobbyController {
    @Inject
    App app;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    UserApiService userApiService;

    @Inject
    Subscriber subscriber;
    @Inject
    LobbyService lobbyService;

    @Inject
    GamesService gamesService;

    @SubComponent
    @Inject
    EnterGameComponent enterGameComponent;

    @SubComponent
    @Inject
    LobbySettingsComponent lobbySettingsComponent;

    @SubComponent
    @Inject
    LobbyHostSettingsComponent lobbyHostSettingsComponent;

    @Inject
    Provider<UserComponent> userComponentProvider;

    @Inject
    EventListener eventListener;

    @FXML
    ListView<MemberUser> playerListView;

    @FXML
    StackPane lobbyElement;

    @Param("gameid")
    String gameID;

    @Param("ashost")
    Boolean asHost;

    Game game;

    private final ObservableList<MemberUser> users = FXCollections.observableArrayList();

    @Inject
    public LobbyController() {

    }

    // TODO: Example method, delete before PR
    @OnInit
    void setToken() {
        tokenStorage.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NjNiNDk3NTVlNmMxYWJiYzA5ZGM3ZjciLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJQeWdtYWxlcyBMaXR0bGUiLCJpYXQiOjE3MTUxNzY0MzgsImV4cCI6MTcxNTE4MDAzOH0.vlSQS46wka13g3XJFTHvax0bq-uMcOHgQZ-PPWjEmJM");
        tokenStorage.setUserId("663b49755e6c1abbc09dc7f7");
    }

    @OnInit
    void init() {
        // TODO: gameID should be transferred from the lobby selection screen
        this.gameID = "663b49085e6c1abbc09dc76f";
        this.subscriber.subscribe(this.gamesService.getGame(gameID), game -> {
            this.game = game;
            this.enterGameComponent.setGameName(game.name());
            this.lobbySettingsComponent.setGameName(game.name());
            this.lobbyHostSettingsComponent.setGameName(game.name());
        });

        this.enterGameComponent.setGameID(this.gameID);
        this.lobbySettingsComponent.setGameID(this.gameID);
        this.lobbyHostSettingsComponent.setGameID(this.gameID);

        this.lobbyService.loadPlayers(this.gameID).subscribe(dto ->
                Arrays.stream(dto).forEach(data -> this.addUserToList(data.user(), data)));

        this.subscriber.subscribe(this.eventListener
                .listen("games." + this.gameID + ".members.*.*", MemberDto.class), event -> {
            String id = event.data().user();
            switch (event.suffix()) {
                case "created" -> {
                    if (this.tokenStorage.getUserId().equals(id))
                        this.lobbyElement.getChildren().add(this.lobbySettingsComponent);
                    this.addUserToList(id, event.data()); }
                case "updated" -> this.replaceUserInList(id, event.data());
                case "deleted" -> this.removeUserFromList(id);
            }
        });
    }

    @OnRender
    void render() {
        this.playerListView.setItems(this.users);
        this.playerListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.userComponentProvider));
        this.setStartingLobbyElement();
    }

    @OnDestroy
    void destroy() {
        subscriber.dispose();
    }

    private void addUserToList(String userID, MemberDto data) {
        this.subscriber.subscribe(this.userApiService.getUser(userID), user ->
                this.users.add(new MemberUser(user, data.ready())));
    }

    private void replaceUserInList(String userID, MemberDto data) {
        this.userApiService.getUser(userID).subscribe(replacer ->
                this.users.replaceAll(memberUser -> memberUser.user()._id().equals(userID)
                        ? new MemberUser(replacer, data.ready()): memberUser));
    }

    private void removeUserFromList(String userID) {
        this.users.removeIf(memberUser -> memberUser.user()._id().equals(userID));
    }

    private void setStartingLobbyElement() {
        this.subscriber.subscribe(this.lobbyService.loadPlayers(this.gameID), dtos -> {
            if (this.tokenStorage.getUserId().equals(this.game.owner())) {
                this.lobbyElement.getChildren().add(this.lobbyHostSettingsComponent);
            } else if (Arrays.stream(dtos).map(MemberDto::user).anyMatch(id -> id.equals(this.tokenStorage.getUserId()))) {
                this.lobbyElement.getChildren().add(this.lobbySettingsComponent);
            } else {
                this.lobbyElement.getChildren().add(this.enterGameComponent);
            }
        });
    }
}
