package de.uniks.stp24.service;

import de.uniks.stp24.dto.LoginDto;
import de.uniks.stp24.dto.LoginResult;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class EditGameService {
    @Inject
    AuthApiService authApiService;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public EditGameService() {
    }

    public Observable<UpdateGameResultDto> editGame(String name, boolean started, int speed, int size, String password){
        return GamesApiService
                .editGame(new UpdateGameDto(name, started, speed, size, password))
                .doOnNext(updateGameResult -> {
                    tokenStorage.setToken(updateGameResult.accessToken());
                    tokenStorage.setUserId(updateGameResult._id());
                });
    }
}
