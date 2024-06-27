package de.uniks.stp24;


import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.service.menu.LoginService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testfx.matcher.base.WindowMatchers;
import org.testfx.util.WaitForAsyncUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;



public class AppTest extends ControllerTest {

    LoginService loginService;
    AuthApiService authApiService;
    GamesApiService gamesApiService;
    GameMembersApiService gameMembersApiService;
    PresetsApiService presetsApiService;
    CreateGameService createGameService;
    EventListener eventListener;
    LobbyService lobbyService;
    UserApiService userApiService;
    TokenStorage tokenStorage;
    EditGameService editGameService;
    EmpireService empireService;
    IslandsService islandsService;

    @Spy
    BubbleComponent bubbleComponent;
    @Spy
    ClockComponent clockComponent;

    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();
    final Subject<Event<MemberDto>> memberSubject = BehaviorSubject.create();


    @BeforeEach
    public void setUp() {
        SignUpResultDto signUpResult = new SignUpResultDto(null, null, "1", "JustATest", null);
        LoginResult loginResult = new LoginResult("1", "JustATest", null, "a", "r");
        LoginDto loginDto = new LoginDto("JustATest", "testpassword");
        RefreshDto refreshDto = new RefreshDto("r");
        Game game1 = new Game("2024-05-28T12:55:25.688Z", null, "1", "Was geht", "1", false, 0,0, null);
        Game game2 = new Game("2024-05-28T13:55:25.688Z", null, "2", "rapapa", "testID", false, 0,0, null);
        Game game3 = new Game("2024-05-28T14:55:25.688Z", null, "123", "AwesomeLobby123", "1", false, 0, 0, null);

        User user = new User("JustATest", "1", null, null, null);

        GameSettings gameSettings = new GameSettings(100);
        CreateGameResultDto createGameResultDto = new CreateGameResultDto("2024-05-28T14:55:25.688Z",null,game3._id(), "AwesomeLobby123","1", false,1, 1, gameSettings);
        CreateGameDto createGameDto = new CreateGameDto("AwesomeLobby123", false, 1, gameSettings, "123");

        MemberDto memberDto = new MemberDto(false, user._id(), null, null);
        MemberDto[] memberDtos = new MemberDto[1];
        memberDtos[0] = memberDto;


        userApiService = testComponent.userApiService();
        doReturn(Observable.just(signUpResult)).when(userApiService).signup(any());


        authApiService = testComponent.authApiService();
        loginService = testComponent.loginService();
        gamesApiService = testComponent.gamesApiService();
        gameMembersApiService = testComponent.gameMemberApiService();
        presetsApiService = testComponent.presetsApiService();
        createGameService = testComponent.createGameService();
        eventListener = testComponent.eventListener();
        lobbyService = testComponent.lobbyService();
        tokenStorage = testComponent.tokenStorage();
        empireService = testComponent.empireService();
        editGameService = testComponent.editGameService();
        islandsService = testComponent.islandsService();

        doReturn(Observable.just(loginResult)).when(authApiService).login(loginDto);
        doReturn(Observable.just(loginResult)).when(authApiService).refresh(refreshDto);

        doReturn(Observable.just(loginResult))
                .when(loginService).login(any(), any(), eq(false));

        doReturn(Observable.just(List.of(
                game1, game2
        ))).when(gamesApiService).findAll();

        doReturn(Observable.just(createGameResultDto)).when(createGameService).createGame(any(), any(), any());

        Event<Game> gameEvent = new Event<>("games." + game3._id() + ".created", new Game("2024-05-28T14:55:25.688Z", null, game3._id(), createGameDto.name(), "1", false, 0,0, null));
        doReturn(Observable.empty()).doReturn(Observable.just(gameEvent)).when(eventListener).listen(eq("games.*.*"), eq(Game.class));

        doReturn(Observable.just(game3)).when(gamesApiService).getGame(game3._id());

        when(lobbyService.loadPlayers(any()))
                .thenReturn(Observable.just(memberDtos))
                .thenReturn(Observable.just(new MemberDto[]{
                new MemberDto(true, "1", null, null)}));


        doReturn(Observable.empty()).when(eventListener).listen(eq("games." + game3._id() + ".deleted"), eq(Game.class));

        doReturn(memberSubject).when(eventListener).listen("games." + game3._id() + ".members.*.*", MemberDto.class);
        doReturn(memberSubject).when(eventListener).listen(eq("games." + game3._id() + ".members.*.updated"), eq(MemberDto.class));


        doReturn(Observable.just(memberDto)).when(lobbyService).getMember(game3._id(), user._id());
        doReturn(Observable.just(new MemberDto(true, user._id(), null, null))).when(lobbyService).updateMember(game3._id(), user._id(), true, null);

        doReturn(true).when(createGameService).nameIsAvailable("AwesomeLobby123");
        doAnswer(new Answer<Observable<User>>() {
            @Override
            public Observable<User> answer(InvocationOnMock invocation) throws Throwable {
                // Hier können wir eine Benachrichtigung senden, dass getUser aufgerufen wurde
                return Observable.just(user);
            }
        }).when(userApiService).getUser(any());

        doReturn("1").when(tokenStorage).getUserId();
        doReturn(Observable.just(new MemberDto(false, user._id(), new Empire("Buccaneers", "", "#DC143C", 0, 0, null,"uninhabitable_0"), null))).when(lobbyService).updateMember(game3._id(),user._id(), false, null);
        doReturn(Observable.just(new MemberDto(true, user._id(), new Empire("Buccaneers", "", "#DC143C", 0, 0, null,"uninhabitable_0"), null))).when(lobbyService).updateMember(game3._id(),user._id(), true, null);
        doReturn(Observable.just(new UpdateGameResultDto("2024-05-28T14:55:25.688Z", null,game3._id(),"testGame", user._id(),
                true, 0, 0, null))).when(this.editGameService).startGame(any());


        doReturn(Observable.just(new ReadEmpireDto[]{new ReadEmpireDto("1","a","testEmpireID", game3._id(),
                user._id(),"tesEmpire","a","#DC143C",0, 0, "uninhabitable_0")})).when(this.empireService).getEmpires(any());

        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        doReturn(Observable.just(new Trait[]{})).when(presetsApiService).getTraitsPreset();
    }

    @Test
    public void v1(){
        goToSignup();
        signupUser();
        loginUser();
        goToNewGame();
        createGame();
        loadGame();
        selectEmpire();
        startAGame();
    }


    /*
    =================================== Navigate methods ===================================
     */


    private void goToSignup(){
        verifyThat(window("Calvario"), WindowMatchers.isShowing());
        clickOn("#press_any_key");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("LOGIN"), WindowMatchers.isShowing());
        clickOn("#signupButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("REGISTER"), WindowMatchers.isShowing());
        WaitForAsyncUtils.waitForFxEvents();
    }


    private void signupUser(){
        clickOn("#usernameField");
        write("JustATest");
        clickOn("#passwordField");
        write("testpassword");
        clickOn("#repeatPasswordField");
        write("testpassword");
        assertEquals(((TextArea) lookup("#captainText").query()).getText(), resources.getString("pirate.register.possible"));

        clickOn("#registerButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("LOGIN"), WindowMatchers.isShowing());
    }

    private void loginUser(){
        clickOn("#loginButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("BROWSE GAME"), WindowMatchers.isShowing());
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void goToNewGame(){
        clickOn("#new_game_b");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("CREATE GAME"), WindowMatchers.isShowing());
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void createGame(){
        clickOn("#createNameTextField");
        write("AwesomeLobby123");
        clickOn("#createPasswordTextField");
        write("123");
        clickOn("#createRepeatPasswordTextField");
        write("123");
        clickOn("#createMapSizeSpinner");
        clickOn("#createGameConfirmButton");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("BROWSE GAME"), WindowMatchers.isShowing());
    }

    private void loadGame(){
        ListView<String> listView = lookup("#gameList").query();
        Node listCell = listView.lookupAll(".list-cell").toArray(new Node[0])[0];
        clickOn(listCell);
        clickOn("#load_game_b");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat(window("ENTER GAME"), WindowMatchers.isShowing());
    }

    private void selectEmpire(){
        clickOn("#selectEmpireButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void startAGame() {
        clickOn("#readyButton");
        this.memberSubject.onNext(new Event<>("games.123.members.1.updated",
                new MemberDto(true, "JustATest", null, null)));
        assertFalse(lookup("#startJourneyButton").queryButton().isDisabled());

        clickOn("#startJourneyButton");
        this.gameSubject.onNext(new Event<>("games.testGameID.updated", new Game("1", "a","testGameID","testGame","testGameHostID",
                true, 1, 0, new GameSettings(1))));
        WaitForAsyncUtils.waitForFxEvents();

        // game screen will not be shown, but loaded
        assertEquals("ENTER GAME",stage.getTitle() );
    }

}
