package de.uniks.stp24;

import dagger.Module;
import dagger.Provides;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.service.*;
import de.uniks.stp24.ws.EventListener;
import org.mockito.Mockito;

import javax.inject.Singleton;

@Module
public class TestModule {
    @Provides
    @Singleton
    AuthApiService authApiService() {
        return Mockito.mock(AuthApiService.class);
    }
    @Provides
    @Singleton
    UserApiService userApiService() {
        return Mockito.mock(UserApiService.class);
    }

    @Provides
    @Singleton
    GamesApiService gamesApiService() {
        return Mockito.mock(GamesApiService.class);
    }

    @Provides
    @Singleton
    GameMembersApiService gameMembersApiService(){return Mockito.mock(GameMembersApiService.class);
    }
    @Provides
    @Singleton
    CreateGameService createGameService(){
        return Mockito.mock(CreateGameService.class);
    }

    @Provides
    @Singleton
    EventListener eventListener(){
        return Mockito.mock(EventListener.class);
    }

    @Provides
    @Singleton
    LoginService loginService(){
        return Mockito.mock(LoginService.class);
    }

    @Provides
    @Singleton
    LobbyService lobbyService(){
        return Mockito.mock(LobbyService.class);
    }

    @Provides
    @Singleton
    TokenStorage tokenStorage(){
        return Mockito.mock(TokenStorage.class);
    }




}


