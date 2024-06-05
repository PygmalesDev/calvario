package de.uniks.stp24;

import dagger.Module;
import dagger.Provides;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GameSystemsApiService;
import org.mockito.Mockito;

@Module
public class TestModule {
    @Provides
    AuthApiService authApiService() {
        return Mockito.mock(AuthApiService.class);
    }
    @Provides
    UserApiService userApiService() {
        return Mockito.mock(UserApiService.class);
    }

    @Provides
    GamesApiService gamesApiService() {
        return Mockito.mock(GamesApiService.class);
    }
  
    @Provides
    GameMembersApiService gameMembersApiService(){
        return Mockito.mock(GameMembersApiService.class);
    }
    @Provides
    GameSystemsApiService gameSystemsApiService(){ return Mockito.mock(GameSystemsApiService.class); }

}
