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
import javafx.scene.layout.Pane;
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
import java.util.Objects;

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
    @FXML
    Pane lobbyMessagePane;
    @FXML
    Pane lobbyMessageElement;

    @Param("gameid")
    String gameID;

    Game game;

    private final ObservableList<MemberUser> users = FXCollections.observableArrayList();

    @Inject
    public LobbyController() {

    }

    // TODO: Example method, delete before PR
    // TODO: gameID should be transferred from the lobby selection screen
    @OnInit
    void setGameID() {
        this.gameID = "663f8fb35e6c1abbc0a2d218";
    }

    /**
     * Loads game data and transfers its ID and name to the subcomponents.
     * Loads lobby members to the member list and adds event listeners.
     * Creates event listeners.
     */
    @OnInit
    void init() {
        this.subscriber.subscribe(this.gamesService.getGame(gameID), game -> {
            this.game = game;
            this.enterGameComponent.setGameName(game.name());
            this.lobbySettingsComponent.setGameName(game.name());
            this.lobbyHostSettingsComponent.setGameName(game.name());
        });

        this.enterGameComponent.setGameID(this.gameID);
        this.lobbySettingsComponent.setGameID(this.gameID);
        this.lobbyHostSettingsComponent.setGameID(this.gameID);

        this.lobbyService.loadPlayers(this.gameID).subscribe(dto -> {
            Arrays.stream(dto).forEach(data -> this.addUserToList(data.user(), data));
            this.sortHostOnTop();
        });

        this.createUserListListener();
        this.createGameDeletedListener();
    }

    /**
     * Creates an event listener for the case, when the game host leaves the lobby and deletes the game.
     */
    private void createGameDeletedListener() {
        this.subscriber.subscribe(this.eventListener
                .listen("games." + this.gameID + ".deleted", Game.class), event ->
        {
            this.lobbyMessagePane.setVisible(true);
            this.lobbyMessageElement.setVisible(true);
        });
    }

    /**
     * Creates an event listener to update the member list.
     */
    private void createUserListListener() {
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
            this.sortHostOnTop();
        });
    }

    /**
     * Loads players to the member list.
     */
    @OnRender
    void render() {
        this.lobbyMessagePane.setVisible(false);
        this.lobbyMessageElement.setVisible(false);

        this.playerListView.setItems(this.users);
        this.playerListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.userComponentProvider));
        this.setStartingLobbyElement();
    }

    /**
     * Displays new users on the member list.
     * If a user is a host of the game, a (Host) suffix will be added to their nickname.
     * Also displays if the player is ready or not.
     * @param userID ID of the player
     * @param data member data containing their readiness state
     */
    private void addUserToList(String userID, MemberDto data) {
        this.subscriber.subscribe(this.userApiService.getUser(userID), user -> {
            if (userID.equals(this.game.owner()))
                this.users.add(new MemberUser(new User(user.name() + " (Host)",
                        user._id(), user.avatar(), user.createdAt(), user.updatedAt()
                ), data.ready()));
            else
                this.users.add(new MemberUser(user, data.ready()));
        });
    }

    /**
     * Updates the readiness state of the player.
     * @param userID ID of the player
     * @param data member data containing their readiness state
     */
    private void replaceUserInList(String userID, MemberDto data) {
        this.userApiService.getUser(userID).subscribe(replacer ->
                this.users.replaceAll(memberUser -> memberUser.user()._id().equals(userID)
                        ? new MemberUser(replacer, data.ready()): memberUser));
    }

    /**
     * Removes a user from the member list if they leave the lobby.
     * @param userID ID of the player
     */
    private void removeUserFromList(String userID) {
        this.users.removeIf(memberUser -> memberUser.user()._id().equals(userID));
    }

    /**
     * When a user transitions to the lobby screen, one of three elements could be shown. <p>
     * If the user is a host of the game, a host settings screen will be displayed. <p>
     * If the user already is in the lobby, a member screen will be displayed. <p>
     * If the user is not in the lobby, an entry screen will be shown.
     */
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

    /**
     * Will be called after changes in the member list.
     * Sorts the host of the lobby to the top of the list.
     */
    private void sortHostOnTop() {
        MemberUser host = this.users.stream().filter(member ->
                member.user().name().contains("Host")).findFirst().orElse(null);
        this.users.removeIf(member -> member.user().name().contains("Host"));
        if (Objects.nonNull(host))
            this.users.addFirst(host);
    }

    public void goBack() {
        this.app.show("/browsegames");
    }

    @OnDestroy
    void destroy() {
        subscriber.dispose();
    }
}
