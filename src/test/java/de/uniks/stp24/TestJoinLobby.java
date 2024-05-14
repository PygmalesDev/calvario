package de.uniks.stp24;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.component.EnterGameComponent;
import de.uniks.stp24.component.LobbyHostSettingsComponent;
import de.uniks.stp24.component.LobbySettingsComponent;
import de.uniks.stp24.controllers.LobbyController;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.GamesService;
import de.uniks.stp24.service.JoinGameService;
import de.uniks.stp24.service.LobbyService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Inject;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestJoinLobby extends ControllerTest {
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    JoinGameService joinGameService;
    @Spy
    UserApiService userApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GamesService gamesService;
    @Spy
    Subscriber subscriber;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    EnterGameComponent enterGameComponent;
    @Spy
    LobbySettingsComponent lobbySettingsComponent;
    @Spy
    LobbyHostSettingsComponent lobbyHostSettingsComponent;
    @InjectMocks
    LobbyController lobbyController;


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);

        // Mock getting userID
        doReturn("111").when(this.tokenStorage).getUserId();

        // Mock getting game
        doReturn(Observable.just(new Game("1", "a","gameID","c","111",
                false, 1, 0, new GameSettings(1))))
                .when(this.gamesService).getGame(any());

        // Mock loading lobby members
        doReturn(Observable.just(new MemberDto[]{new MemberDto(false, "111",
                                new Empire("e", "a", "b",
                                        1, 2, "h", new String[]{"a"}), "1")}))
                .when(this.lobbyService).loadPlayers(any());

        // Mock getting user
        doReturn(Observable.just(new User("TestSubject", "111", null, "1", "1")))
                .when(this.userApiService).getUser(any());

        // Mock getting members updates (In controller)
        doReturn(Observable.just(new MemberDto(false, "111",
                new Empire("e", "a", "b",
                        1, 2, "h", new String[]{"a"}), "1")))
                .when(this.eventListener).listen(eq("games.null.members.*.*"), eq(MemberDto.class));

        // Mock getting members readiness updates (In subcomponent)
        doReturn(Observable.just(new MemberDto(false, "111",
                new Empire("e", "a", "b",
                        1, 2, "h", new String[]{"a"}), "1")))
                .when(this.eventListener).listen(eq("games.null.members.*.updated"), eq(MemberDto.class));

        // Mock deleting game
        doReturn(Observable.just(new Game("1", "a","b","c","111",
                        false, 1, 0, new GameSettings(1))))
                .when(this.eventListener).listen(eq("games.null.deleted"), eq(Game.class));

        this.app.show(this.lobbyController);
    }

    @Test
    public void testJoinLobbyAsHost() {
        doReturn(null).when(app).show("/browseGames");
    }
}
