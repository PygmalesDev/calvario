package de.uniks.stp24.rest;

import de.uniks.stp24.dto.Game;
import de.uniks.stp24.dto.GameDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface GamesApiService {
    @GET("/games")
    Observable<Game> games(@Body GameDto gameDto);
}
