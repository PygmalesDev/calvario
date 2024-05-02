package de.uniks.stp24;

import dagger.Module;
import dagger.Provides;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.UserApiService;
import org.mockito.Mockito;

@Module
public class TestModule {
    @Provides
    AuthApiService authApiService(){
        return Mockito.mock(AuthApiService.class);
    }

    @Provides
    UserApiService userApiService(){
        return Mockito.mock(UserApiService.class);
    }

}
