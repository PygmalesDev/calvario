package de.uniks.stp24;

import dagger.Component;
import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.dagger.MainComponent;
import de.uniks.stp24.dagger.MainModule;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.*;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.service.menu.LoginService;
import de.uniks.stp24.ws.EventListener;

import javax.inject.Singleton;

@Component(modules = {MainModule.class, TestModule.class})
@Singleton
public interface TestComponent extends MainComponent {
    AuthApiService authApiService();
    UserApiService userApiService();
    GamesApiService gamesApiService();
    LoginService loginService();
    CreateGameService createGameService();
    EventListener eventListener();
    LobbyService lobbyService();
    GameMembersApiService gameMemberApiService();
    TokenStorage tokenStorage();
    EditGameService editGameService();
    EmpireService empireService();

    @Component.Builder
    interface Builder extends MainComponent.Builder {
        @Override
        TestComponent build();
    }
}