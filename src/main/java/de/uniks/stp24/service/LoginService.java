package de.uniks.stp24.service;

import de.uniks.stp24.dto.LoginDto;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.dto.RefreshDto;
import de.uniks.stp24.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {
    @Inject
    AuthApiService authApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    PrefService prefService;

    @Inject
    public LoginService() {
    }

    public boolean autoLogin() {
        // checks if user wants to log in automatically: there is a refreshToken if the user selected remember me before
        final String refreshToken = prefService.getRefreshToken();
        if (refreshToken == null || System.getenv("DISABLE_AUTO_LOGIN") != null) {
            // no automatic login possible
            return false;
        }
        try {
            // login with saved refreshToken
            final LoginResult result = authApiService.refresh(new RefreshDto(refreshToken)).doOnError(
              error -> System.out.println("FEHLER AUTOLOGIN")
            ).blockingFirst();
            tokenStorage.setToken(result.accessToken());
            System.out.println(result.accessToken());
            tokenStorage.setUserId(result._id());
            tokenStorage.setName(result.name());
            tokenStorage.setAvatar(result.avatar());
            prefService.setRefreshToken(result.refreshToken());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public Observable<LoginResult> login(String username, String password, boolean rememberMe) {
        return authApiService
                .login(new LoginDto(username, password))
                .doOnNext(loginResult -> {
                    tokenStorage.setToken(loginResult.accessToken());
                    tokenStorage.setUserId(loginResult._id());
                    tokenStorage.setName(loginResult.name());
                    tokenStorage.setAvatar(loginResult.avatar());
                    if (rememberMe) {
                        prefService.setRefreshToken(loginResult.refreshToken());
                    }
                });
    }

}
