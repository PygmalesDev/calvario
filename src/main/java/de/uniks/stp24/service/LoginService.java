package de.uniks.stp24.service;

import de.uniks.stp24.dto.LoginDto;
import de.uniks.stp24.dto.LoginResult;
import de.uniks.stp24.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {
    @Inject
    AuthApiService authApiService;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public LoginService() {
    }



    public Observable<LoginResult> login(String username, String password){
        return authApiService
                .login(new LoginDto(username, password))
                .doOnNext(loginResult -> {
                    tokenStorage.setToken(loginResult.accessToken());
                    tokenStorage.setUserId(loginResult._id());
                });
    }

}
