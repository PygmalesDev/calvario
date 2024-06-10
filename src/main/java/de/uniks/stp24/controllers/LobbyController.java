package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.controllers.helper.JoinGameHelper;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;

@Title("%enter.game")
@Controller
public class LobbyController extends BasicController {

    @Inject
    EmpireService empireService;
    @Inject
    IslandsService islandsService;
    @Inject
    UserApiService userApiService;
    @Inject
    LobbyService lobbyService;
    @Inject
    GamesService gamesService;
    @Inject
    public JoinGameHelper joinGameHelper;
    @SubComponent
    @Inject
    public EnterGameComponent enterGameComponent;
    @SubComponent
    @Inject
    public LobbySettingsComponent lobbySettingsComponent;
    @SubComponent
    @Inject
    public LobbyHostSettingsComponent lobbyHostSettingsComponent;
    @SubComponent
    @Inject
    public UserComponent userComponent;
    @Inject
    public Provider<UserComponent> userComponentProvider;
    @Inject
    EventListener eventListener;
    @FXML
    public ListView<MemberUser> playerListView;
    @FXML
    Text messageText;
    @FXML
    public StackPane lobbyElement;
    @FXML
    Pane lobbyMessagePane;
    @FXML
    Pane lobbyMessageElement;
    @FXML
    Pane captainContainer;
    @FXML
    Text gameNameField;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    @Inject
    @Resource
    public ResourceBundle resources;

    @Param("gameid")
    String gameID;

    Game game;

    private final ObservableList<MemberUser> users = FXCollections.observableArrayList();
    private boolean asHost;
    private boolean wasKicked;
    private boolean isHostReady;
    public int maxMember;
    private Button[] lobbyButtons;


    @Inject
    public LobbyController() {

    }

    /**
     * Loads game data and transfers its ID and name to the subcomponents.
     * Loads lobby members to the member list and adds event listeners.
     * Creates event listeners.
     */
    @OnInit
    void init() {
        this.subscriber.subscribe(this.gamesService.getGame(this.gameID),
          game -> {
            this.game = game;
            this.gameID = game._id();
            this.asHost = game.owner().equals(this.tokenStorage.getUserId());
            // Todo: add maxMember to Game Model -> this.maxMember = game.maxMember();
              this.maxMember = 2;
            this.lobbyHostSettingsComponent.setMaxMember(this.maxMember);
            this.lobbyHostSettingsComponent.setBubbleComponent(this.bubbleComponent);

            this.enterGameComponent.setGameID(this.gameID);
            this.lobbySettingsComponent.setGameID(this.gameID);
            this.lobbyHostSettingsComponent.setGameID(this.gameID);

            this.enterGameComponent.errorMessage.textProperty().addListener(((observable, oldValue, newValue) -> {
                this.bubbleComponent.setErrorMode(true);
                this.bubbleComponent.setCaptainText(newValue);
            }));
            this.gameNameField.setText(this.game.name());
            this.createUserListListener();
            this.createGameDeletedListener();
            this.lobbyService.loadPlayers(this.gameID).subscribe(dto -> {
                Arrays.stream(dto).forEach(data -> {
                    this.addUserToList(data.user(), data);
                    if (data.user().equals(this.game.owner()))
                        this.isHostReady = data.ready();
                    if (data.user().equals(this.tokenStorage.getUserId())){
                            this.lobbySettingsComponent.setReadyButton(data.ready());
                            this.lobbyHostSettingsComponent.setReadyButton(data.ready());
                        }
                });

                // check if game can be started
                boolean allReady = Arrays.stream(dto)
                        .map(MemberDto::ready)
                        .reduce(Boolean::logicalAnd)
                        .orElse(true);
                int countEmpires = (int) Arrays.stream(dto)
                        .filter(member -> member.empire() != null)
                        .count();
                boolean tooManyPlayer = countEmpires > this.maxMember;
                if(tooManyPlayer){
                    bubbleComponent.setCaptainText(resources.getString("pirate.enterGame.too.many.players"));
                }
                this.lobbyHostSettingsComponent.startJourneyButton.setDisable(!allReady || tooManyPlayer);

                this.sortMemberList();
            },
              this::errorMsg);
            this.createGameStartedListener();
            this.lobbyHostSettingsComponent.createCheckPlayerReadinessListener();
            },
          this::errorMsg);
    }

    /**
     * Creates an event listener for the case, when the game host leaves the lobby and deletes the game.
     */
    private void createGameDeletedListener() {
        this.subscriber.subscribe(this.eventListener
                .listen("games." + this.gameID + ".deleted", Game.class),
          event -> {
            this.lobbyMessagePane.setVisible(true);
            this.lobbyMessageElement.setVisible(true);
            this.messageText.setText(resources.getString("lobby.has.been.deleted"));
          },
          this::errorMsg);
    }

    /**
     * Creates an event listener to update the member list.
     */
    private void createUserListListener() {
        this.subscriber.subscribe(this.eventListener
                .listen("games." + this.gameID + ".members.*.*", MemberDto.class),
          event -> {
            String id = event.data().user();
            switch (event.suffix()) {
                case "created" -> {
                    if (this.tokenStorage.getUserId().equals(id)) {
                        this.lobbyElement.getChildren().remove(this.enterGameComponent);
                        this.lobbyElement.getChildren().add(this.lobbySettingsComponent);
                        this.bubbleComponent.setErrorMode(false);
                        this.bubbleComponent.setCaptainText(resources.getString("pirate.enterGame.next.move"));
                    }
                    this.addUserToList(id, event.data());
                }
                case "updated" -> this.replaceUserInList(id, event.data());
                case "deleted" -> this.removeUserFromList(id);
            }
            this.sortMemberList();
        },
          this::errorMsg
          );
    }

    /**
     * Creates an event listener that sends all members to ingame when the game is started.
     * after game is started:
     * gameId, empireId and flags are saved in TokenStorage (in joinGameHelper)
     * island information (IslandsService) must be retrieved (in islandsService)
     * change to game screen occurs in islandsService
     */
    private void createGameStartedListener(){
        this.subscriber.subscribe(this.eventListener.listen("games." + this.gameID + ".updated", Game.class),
             event -> {
                buttonsSetDisability(true);
                if(event.data().started()) {
                    subscriber.subscribe(lobbyService.getMember(this.gameID, this.tokenStorage.getUserId()),
                        memberDto -> this.joinGameHelper.joinGame(this.gameID),
                        this::errorMsg);
                }
            }, error -> {
                buttonsSetDisability(false);
                errorMsg(error);
            });
    }

    private void buttonsSetDisability(boolean disable){
        for(Button button : this.lobbyButtons){
            button.setDisable(disable);
        }
    }

    @OnRender
    void getLobbyButtons(){
        this.lobbyButtons =  new Button[]{
                this.lobbyHostSettingsComponent.startJourneyButton,
                this.lobbyHostSettingsComponent.selectEmpireButton,
                this.lobbySettingsComponent.selectEmpireButton,
                this.lobbySettingsComponent.readyButton,
                this.lobbyHostSettingsComponent.readyButton,
                this.lobbySettingsComponent.leaveLobbyButton,
                this.lobbyHostSettingsComponent.closeLobbyButton
        };
    }

    /**
     * Loads players to the member list.
     */
    @OnRender
    void render() {
        this.lobbyMessagePane.setVisible(false);
        this.lobbyMessageElement.setVisible(false);
        this.playerListView.setItems(this.users);
        this.captainContainer.getChildren().add(this.bubbleComponent);
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
            String suffix = "";
            if (userID.equals(this.game.owner())) suffix += " (Host)";
            if (Objects.isNull(data.empire())) suffix += " (Spectator)";

            this.users.add(new MemberUser(new User(user.name() + suffix,
                    user._id(), user.avatar(), user.createdAt(), user.updatedAt()
            ), data.empire(), data.ready(), this.game, this.asHost));
        },
          this::errorMsg);
    }

    /**
     * Updates the readiness state of the player.
     * @param userID ID of the player
     * @param data member data containing their readiness state
     */
    private void replaceUserInList(String userID, MemberDto data) {
        if (data.user().equals(this.game.owner()))
            this.isHostReady = data.ready();

        this.users.replaceAll(memberUser -> {
            if (memberUser.user()._id().equals(userID)) {
                if (Objects.nonNull(data.empire())) {
                    return new MemberUser(new User(
                            memberUser.user().name().replace(" (Spectator)", ""),
                            userID, memberUser.user().avatar(), memberUser.user().createdAt(),
                            memberUser.user().updatedAt()), data.empire(), data.ready(), this.game, this.asHost);
                } else {
                    String suffix = " (Spectator)";
                    if (memberUser.user().name().contains("(Spectator)"))
                        suffix = "";
                    return new MemberUser(new User(
                            memberUser.user().name() + suffix, userID, memberUser.user().avatar(),
                            memberUser.user().createdAt(), memberUser.user().updatedAt()),
                            null, data.ready(), this.game, this.asHost);
                }
            } else
                return memberUser;
        });
    }

    /**
     * Removes a user from the member list if they leave the lobby.
     * @param userID ID of the player
     */
    private void removeUserFromList(String userID) {
        if (!this.lobbySettingsComponent.leftLobby && this.tokenStorage.getUserId().equals(userID)) {
            this.messageText.setText(resources.getString("kicked.from.lobby"));
            this.lobbyMessagePane.setVisible(true);
            this.lobbyMessageElement.setVisible(true);
            this.wasKicked = true;
        }
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
                bubbleComponent.setCaptainText(resources.getString("pirate.enterGame.next.move"));
                this.lobbyElement.getChildren().add(this.lobbyHostSettingsComponent);
            } else if (Arrays.stream(dtos).map(MemberDto::user).anyMatch(id -> id.equals(this.tokenStorage.getUserId()))) {
                bubbleComponent.setCaptainText(resources.getString("pirate.enterGame.next.move"));
                this.lobbyElement.getChildren().add(this.lobbySettingsComponent);
            } else {
                bubbleComponent.setCaptainText(resources.getString("pirate.enterGame.password"));
                this.lobbyElement.getChildren().add(this.enterGameComponent);
            }
        },
          this::errorMsg);
    }

    /**
     * Will be called after changes in the member list.
     * Sorts the host of the lobby to the top of the list.
     */
    private void sortMemberList() {
        this.users.sort(Comparator.comparing(MemberUser::ready).reversed());
        this.users.sort(Comparator.comparing(member -> !member.user()._id().equals(this.game.owner())));
    }

    public void goBack() {
        if (!this.wasKicked) this.subscriber.subscribe(
                this.lobbyService.leaveLobby(this.gameID, this.tokenStorage.getUserId()),
                result -> this.app.show("/browseGames"),
                this::errorMsg);
        else
            this.app.show("/browseGames");
    }


    @OnDestroy
    void destroy() {
        subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }

    private void errorMsg(@NotNull Throwable error) {
        this.enterGameComponent.errorMessage.textProperty().set(getErrorInfoText(error));
    }
}