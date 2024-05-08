package de.uniks.stp24.service;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class GamesService {
    @Inject
    public GamesService() {

    }

    @Inject
    GamesApiService gamesApiService;

    public Observable<Game> getGame(String gameID) {
        return gamesApiService.getGame(gameID);
    }
}
