package de.uniks.stp24.rest;

import de.uniks.stp24.dto.CreateGameDto;
import de.uniks.stp24.dto.CreateGameResultDto;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface GamesApiService {
    @PATCH("games/{id}")
    Observable<UpdateGameResultDto> editGame(@Path("id") String id, @Body UpdateGameDto dto);

    @POST("games")
    Observable<CreateGameResultDto> createGame(@Body CreateGameDto dto);

    @POST("games")
    Observable<Game> create(@Body CreateGameDto dto);

    @GET("games")
    Observable<List<Game>> findAll();

    @GET("games")
    Observable<List<Game>> findAll(@Query("ids") List<String> ids);

    @GET("games/{id}")
    Observable<Game> getGame(@Path("id") String gameID);

    @DELETE("games/{id}")
    Observable<Game> deleteGame(@Path("id") String id);
}


