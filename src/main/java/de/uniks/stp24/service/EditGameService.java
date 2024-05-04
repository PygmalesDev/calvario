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
    GamesApiService gamesApiService;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public EditGameService() {
    }

    public Observable<UpdateGameResultDto> editGame(String name, int size, String password){
        return gamesApiService
                .editGame(new UpdateGameDto(name, size, password))
                .doOnNext(updateGameResult -> {
                    //tokenStorage.setToken(updateGameResult.);
                    //tokenStorage.setUserId(updateGameResult._id());
                });
    }
}
