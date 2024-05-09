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
    LogoutService(){}

    public Observable<LogoutResult> logout(String any) {
        return authApiService.logout(new LogoutDto(any))
                .doOnNext( result -> {
                });
    }
}
