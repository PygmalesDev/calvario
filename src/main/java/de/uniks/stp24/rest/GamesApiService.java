package de.uniks.stp24.rest;

import de.uniks.stp24.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GamesApiService {
    @GET("games/{id}")
    Observable<Game> getGame(@Path("id") String gameID);

    @DELETE("games/{id}")
    Observable<Game> deleteGame(@Path("id") String gameID);

}
