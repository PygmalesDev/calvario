package de.uniks.stp24;

import dagger.Module;
import dagger.Provides;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.service.menu.LoginService;
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
    GameMembersApiService gameMembersApiService(){
        return Mockito.mock(GameMembersApiService.class);
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
    JobsService jobsService() {
        return Mockito.mock(JobsService.class);
    };

    @Provides
    @Singleton
    JobsApiService jobsApiService() {return Mockito.mock(JobsApiService.class);}

    @Provides
    GameSystemsApiService gameSystemsApiService(){ return Mockito.mock(GameSystemsApiService.class); }

    @Provides
    @Singleton
    EmpireApiService empireApiService() {
        return Mockito.mock(EmpireApiService.class);
    }

    @Provides
    @Singleton
    TokenStorage tokenStorage(){
        return Mockito.mock(TokenStorage.class);
    }

    @Provides
    @Singleton
    EmpireService empireService(){
        return Mockito.mock(EmpireService.class);
    }

    @Provides
    @Singleton
    EditGameService editGameService(){
        return Mockito.mock(EditGameService.class);
    }

    @Provides
    @Singleton
    PresetsApiService presetsApiService(){return Mockito.mock(PresetsApiService.class);}

}


