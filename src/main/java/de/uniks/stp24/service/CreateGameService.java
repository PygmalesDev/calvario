package de.uniks.stp24.service;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class CreateGameService {
    @Inject
    GamesApiService gamesApiService;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public CreateGameService() {
    }

    public Observable<CreateGameResultDto> createGame(String name, int size, String password){
        return gamesApiService
                .createGame(new CreateGameDto("Bambus", false,1, 100,   "kartoffel"))
                .doOnNext(createGameResult -> {
                    System.out.println(createGameResult);
                });
    }
}
