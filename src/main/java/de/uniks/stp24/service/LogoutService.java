package de.uniks.stp24.service;

import de.uniks.stp24.dto.LogoutDto;
import de.uniks.stp24.model.LogoutResult;
import de.uniks.stp24.rest.AuthApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LogoutService {

    @Inject
    AuthApiService authApiService;
    @Inject
    PrefService prefService;

    @Inject
    LogoutService() {

    }

    // refreshToken will be now remove
    // this way the app shouldn't try to autologin on next start
    public Observable<LogoutResult> logout(String any) {
        return authApiService.logout(new LogoutDto(any))
                .doFinally( () -> prefService.removeRefreshToken());
    }
}
