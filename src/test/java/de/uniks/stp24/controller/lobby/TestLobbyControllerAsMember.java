
package de.uniks.stp24.controller.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.*;
import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.*;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestLobbyControllerAsMember extends ControllerTest {
    @Spy
    TokenStorage tokenStorage;
    @Spy
    GamesApiService gamesApiService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    ImageCache imageCache;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    AuthApiService authApiService;
    @Spy
    JoinGameService joinGameService;
    @Spy
    UserApiService userApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GamesService gamesService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);

    Provider<UserComponent> userComponentProvider = ()->{
        final UserComponent userComponent = new UserComponent(imageCache, resources);
        return new UserComponent(imageCache, resources);
    };

    @InjectMocks
    UserComponent userComponent;
    @InjectMocks
    EnterGameComponent enterGameComponent;
    @InjectMocks
    LobbySettingsComponent lobbySettingsComponent;
    @InjectMocks
    LobbyHostSettingsComponent lobbyHostSettingsComponent;
    @InjectMocks
    LobbyController lobbyController;
    @InjectMocks
    BubbleComponent bubbleComponent;

    final Subject<Event<MemberDto>> memberSubject = BehaviorSubject.create();
    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);

        this.lobbyController.bubbleComponent = this.bubbleComponent;
        this.lobbyController.lobbyHostSettingsComponent = this.lobbyHostSettingsComponent;
        this.lobbyController.lobbySettingsComponent = this.lobbySettingsComponent;
        this.lobbyController.enterGameComponent = this.enterGameComponent;
        this.lobbyController.userComponent = this.userComponent;
        this.lobbyController.userComponentProvider = this.userComponentProvider;

        // Mock getting userID
        doReturn("testMemberUnoID").when(this.tokenStorage).getUserId();

        // Mock getting game
        doReturn(Observable.just(new Game("1", "a","testGameID","testGame","testGameHostID",
                false, 1, 0, new GameSettings(1))))
                .when(this.gamesService).getGame(any());

        // Mock listen to game deletion
        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.deleted"), eq(Game.class));

        // Mock loading lobby members
        doReturn(Observable.just(new MemberDto[]{
                new MemberDto(false, "testGameHostID", null, "88888888"),
                new MemberDto(false, "testMemberUnoID", null, "88888888"),
                new MemberDto(false, "testMemberDosID", null, "88888888")
        })).when(this.lobbyService).loadPlayers(any());


        // Mock getting user
        when(this.userApiService.getUser(any()))
                .thenReturn(Observable.just(new User("gameHost", "testGameHostID", null, "1", "1")))
                .thenReturn(Observable.just(new User("testMemberUno", "testMemberUnoID", null, "1", "1")))
                .thenReturn(Observable.just(new User("testMemberDos", "testMemberDosID", null, "1", "1")));

        // Mock getting members updates
        doReturn(memberSubject).when(this.eventListener).listen(eq("games.testGameID.members.*.*"), eq(MemberDto.class));

        // Mock getting members readiness updates
        doReturn(memberSubject).when(this.eventListener).listen(eq("games.testGameID.members.*.updated"), eq(MemberDto.class));

        this.app.show(this.lobbyController);
    }

    /**
     * Tests the behavior of the lobby when the joining player is the host of this game.
     */
    @Test
    public void testJoinLobbyAsMember() {
        WaitForAsyncUtils.waitForFxEvents();

        // Test if one of the users is a joined member
        User member = this.lobbyController.playerListView.getItems().stream()
                .map(MemberUser::user)
                .filter(user -> user._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertEquals(3, this.lobbyController.playerListView.getItems().size());
        assertTrue(member.name().contains("testMemberUno"));
        assertTrue(member.name().contains("(Spectator)"));

        // Test if the correct component is shown to the member
        Node component = this.lobbyController.lobbyElement.getChildren().get(0);
        assertEquals(LobbySettingsComponent.class, component.getClass());
    }

    /**
     * Test the proper removal of the user from member list after leaving.
     */
    @Test
    public void testLeaveLobbyAsMember() {
        doReturn(null).when(this.app).show("/browseGames");
        doReturn(Observable.just(new JoinGameDto())).when(this.lobbyService).leaveLobby(any(), any());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#leaveLobbyButton");

        this.memberSubject.onNext(new Event<>("games.testGameID.members.*.deleted",
                new MemberDto(false, "testMemberUnoID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, this.lobbyController.playerListView.getItems().size());
        assertFalse(this.lobbyController.playerListView.getItems().stream()
                .map(MemberUser::user).map(User::_id)
                .anyMatch(id -> id.equals("tetMemberUnoID"))
        );

        verify(this.eventListener, times(1)).listen("games.testGameID.members.*.*", MemberDto.class);
        verify(this.app, times(1)).show("/browseGames");
    }

    /**
     * Test proper changing of member's status after pressing the ready button on certain occasions.
     */
    @Test
    public void testPressReadyAsMember() {
        doReturn(Observable.just(new MemberDto(false, "testMemberUnoID", null, "88888888")))
                .when(this.lobbyService).getMember(any(), any());

        doReturn(Observable.just(new MemberDto(false, "testMemberUnoID", null, "88888888")))
                .when(this.lobbyService).updateMember(anyString(), anyString(), anyBoolean(), any());

        MemberUser member = this.lobbyController.playerListView.getItems().stream()
                .filter(memberUser -> memberUser.user()._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertFalse(member.ready());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#readyButton");

        // Test readiness update on user that has not yet selected an empire
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(true, "testMemberUnoID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        member = this.lobbyController.playerListView.getItems().stream()
                .filter(memberUser -> memberUser.user()._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertTrue(member.ready());
        assertTrue(member.user().name().contains("(Spectator)"));

        // Test readiness update on user that has selected an empire
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(false, "testMemberUnoID",
                        new Empire(null, null, null, 0, 0, null, null),
                        "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#readyButton");
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(true, "testMemberUnoID",
                        new Empire(null, null, null, 0, 0, null, null),
                        "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        member = this.lobbyController.playerListView.getItems().stream()
                .filter(memberUser -> memberUser.user()._id().equals("testMemberUnoID"))
                .findFirst().orElseThrow();
        assertTrue(member.ready());
        assertFalse(member.user().name().contains("(Spectator)"));
    }

    /**
     * Tests proper switching to the empire selection screen.
     */
    @Test
    public void testSwitchToEmpireSelection() {
        doReturn(null).when(this.app).show(eq("/creation"), any());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#selectEmpireButton");


        WaitForAsyncUtils.waitForFxEvents();

        verify(this.app, times(1)).show(eq("/creation"), any());
    }


    /**
     * Tests being kicked from the lobby by host.
     */
    @Test
    public void testBeingKickedFromLobbyByHost() {
        WaitForAsyncUtils.waitForFxEvents();

        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.deleted",
                new MemberDto(true, "testMemberUnoID", new Empire(null, null, null,
                        0, 0, null, null), "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#lobbyMessageElement").query().isVisible());
        assertTrue(lookup("#messageText").queryText().getText().contains("kicked"));
    }

    /**
     * Test the notification showing after the host has left the lobby.
     */
    @Test
    public void testHostLeftTheLobby() {
        WaitForAsyncUtils.waitForFxEvents();

        this.memberSubject.onNext(new Event<>("games.testGameID.members.testGameHostID.updated",
                new MemberDto(false, "testGameHostID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#lobbyMessageElement").query().isVisible());
        assertTrue(lookup("#messageText").queryText().getText().contains("LEFT THE LOBBY"));
    }

    /**
     * Test the proper message appearing if the host deletes the lobby.
     */
    @Test
    public void testOnLobbyDeletion() {
        doReturn(null).when(this.app).show("/browseGames");
        doReturn(Observable.just(new JoinGameDto()))
                .when(this.lobbyService).leaveLobby(any(), any());

        WaitForAsyncUtils.waitForFxEvents();

        this.gameSubject.onNext(new Event<>("games.testGameID.deleted",
                new Game("1", "a","testGameID","testGame","testGameHostID",
                        false, 1, 0, new GameSettings(1))));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#lobbyMessageElement").query().isVisible());
        assertTrue(lookup("#messageText").queryText().getText().contains("deleted"));
        clickOn("#returnButton");

        verify(this.app, times(1)).show("/browseGames");
    }
}

