package de.uniks.stp24.service;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

public class GamesService {
    @Inject
    public GamesService() {

    }

    @Inject
    GamesApiService gamesApiService;

    public Observable<Game> getGame(String gameID) {
        return this.gamesApiService.getGame(gameID);
    }

    public Observable<Game> deleteGame(String gameID) {
        return this.gamesApiService.deleteGame(gameID);
    }
}
