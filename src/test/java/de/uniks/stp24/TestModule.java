package de.uniks.stp24;

import dagger.Module;
import dagger.Provides;
import de.uniks.stp24.model.User;
import de.uniks.stp24.rest.*;
import org.mockito.Mockito;
import dagger.Provides;
import de.uniks.stp24.rest.UserApiService;
import org.mockito.Mockito;

@Module
public class TestModule {
    @Provides
    GamesApiService gamesApiService(){
        return Mockito.mock(GamesApiService.class);
    }
    @Provides
    GameMembersApiService gameMembersApiService(){
        return Mockito.mock(GameMembersApiService.class);
    }
    @Provides
    AuthApiService authApiService(){
        return Mockito.mock(AuthApiService.class);
    }
    @Provides
    UserApiService userApiService(){
        return Mockito.mock(UserApiService.class);
    }

}
