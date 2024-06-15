package de.uniks.stp24.controller.lobby;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.*;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.JoinGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Node;
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
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestLobbyControllerAsHost extends ControllerTest {
    @Spy
    TokenStorage tokenStorage;
    @Spy
    GamesApiService gamesApiService;
    @Spy
    EmpireApiService empireApiService;
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
    EditGameService editGameService;
    @Spy
    UserApiService userApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GamesService gamesService;
    @Spy
    EmpireService empireService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);

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

    Provider<UserComponent> userComponentProvider = ()-> new UserComponent(imageCache, resources);

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
        doReturn("testGameHostID").when(this.tokenStorage).getUserId();

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

        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.updated"),eq(Game.class));
    }

    /**
     * Tests the behavior of the lobby when the joining player is the host of this game.
     */
    @Test
    public void testJoinLobbyAsHost() {
        WaitForAsyncUtils.waitForFxEvents();

        // Test if the user on top of the member list is the actual host of the game
        User host = this.lobbyController.playerListView.getItems().get(0).user();
        assertEquals(3, this.lobbyController.playerListView.getItems().size());
        assertTrue(host.name().contains("gameHost"));
        assertTrue(host.name().contains("(Host)"));

        // Test if the correct component is shown to the host
        Node component = this.lobbyController.lobbyElement.getChildren().get(0);
        assertEquals(LobbyHostSettingsComponent.class, component.getClass());
        assertTrue(lookup("#startJourneyButton").queryButton().isDisabled());
    }

    /**
     * Tests proper game to be ready for start only after all the members have clicked the ready button.
     */
    @Test
    public void testStartGameAsHost() {

        Empire testEmpire = new Empire("testEmpire", "a","a", 1,  1, new String[]{"1"}, "a");
        doReturn(null).when(this.app).show("/ingame");
        doReturn(Observable.just(new MemberDto(false, "testGameHostID", testEmpire, "88888888")))
                .when(this.lobbyService).getMember(any(), any());

        doReturn(Observable.just(new MemberDto(false, "testGameHostID", null, "88888888")))
                .when(this.lobbyService).updateMember(anyString(), anyString(), anyBoolean(), any());


        when(this.lobbyService.loadPlayers(any()))
                .thenReturn(Observable.just(new MemberDto[]{
                        new MemberDto(false, "testGameHostID", testEmpire, "88888888"),
                        new MemberDto(true, "testMemberUnoID", null, "88888888"),
                        new MemberDto(false, "testMemberDosID", null, "88888888")}))
                .thenReturn(Observable.just(new MemberDto[]{
                        new MemberDto(false, "testGameHostID", testEmpire, "88888888"),
                        new MemberDto(true, "testMemberUnoID", null, "88888888"),
                        new MemberDto(true, "testMemberDosID", null, "88888888")}))
                .thenReturn(Observable.just(new MemberDto[]{
                        new MemberDto(true, "testGameHostID", testEmpire, "88888888"),
                        new MemberDto(true, "testMemberUnoID", null, "88888888"),
                        new MemberDto(true, "testMemberDosID", null, "88888888")}));

        doReturn(Observable.just(new UpdateGameResultDto("1", "a","testGameID","testGame","testGameHostID",
                        true, 1, 0, new GameSettings(1)))).when(this.editGameService).startGame(any());


        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", "testGameID",
                "testGameHostID","testGame","a","a",1, 2, "a")})).when(this.empireService).getEmpires(any());

        doAnswer(show-> {app.show("/ingame");
            return null;
        }).when(this.islandsService).retrieveIslands(any());

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#startJourneyButton").queryButton().isDisabled());
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberUnoID.updated",
                new MemberDto(true, "testMemberUnoID", null, "88888888")));
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#startJourneyButton").queryButton().isDisabled());

        this.memberSubject.onNext(new Event<>("games.testGameID.members.testMemberDosID.updated",
                new MemberDto(true, "testMemberDosID", null, "88888888")));
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#startJourneyButton").queryButton().isDisabled());

        clickOn("#readyButton");
        this.memberSubject.onNext(new Event<>("games.testGameID.members.testGameHostID.updated",
                new MemberDto(true, "testGameHostID", null, "88888888")));

        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(lookup("#startJourneyButton").queryButton().isDisabled());

        clickOn("#startJourneyButton");
        this.gameSubject.onNext(new Event<>("games.testGameID.updated", new Game("1","a",
          "testGameID","testGame", "testGameHostID", true, 1, 0 , new GameSettings(1))));
        WaitForAsyncUtils.waitForFxEvents();
        verify(this.app,times(1)).show("/ingame");

    }

    /**
     * Test leaving the lobby as host.
     */
    @Test
    public void testLeaveLobbyAsHost() {
        doReturn(null).when(this.app).show("/browseGames");

        doReturn(Observable.just(new MemberDto(false, "testGameHostID", null, "88888888")))
                .when(this.lobbyService).getMember(any(), any());

        doReturn(Observable.just(new MemberDto(false, "testGameHostID", null, "88888888")))
                .when(this.lobbyService).updateMember(anyString(), anyString(), anyBoolean(), any());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeLobbyButton");

        WaitForAsyncUtils.waitForFxEvents();
        verify(this.app, times(1)).show("/browseGames");
    }

    /**
     * Tests proper switching to the empire selection screen.
     */
    @Test
    public void testSwitchToEmpireSelection() {
        doReturn(null).when(this.app).show(eq("/creation"), any());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#selectEmpireButton");

        verify(this.app, times(1)).show(eq("/creation"), any());
    }
}
