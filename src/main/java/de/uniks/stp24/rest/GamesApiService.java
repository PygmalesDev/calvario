package de.uniks.stp24.rest;
import de.uniks.stp24.dto.CreateGameResultDto;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import de.uniks.stp24.dto.CreateGameDto;
import de.uniks.stp24.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface GamesApiService {
	@POST("games")
    Observable<UpdateGameResultDto> editGame(@Body UpdateGameDto dto);

    @POST("games")
    Observable<CreateGameResultDto> createGame(@Body CreateGameDto dto);
    
    @POST("games")
    Observable<Game> create (@Body CreateGameDto dto);

    @GET("games")
    Observable<List<Game>> findAll();

    @GET("games")
    Observable<List<Game>> findAll(@Query("ids") List<String> ids);

    /*
    @GET("games/{id}")
    Observable<Game> updateOne(@Path("id") String id, @Body UpdateGamesDTO dto);

     */

    @DELETE("games/{id}")
    Observable<Game> deleteOne(@Path("id") String id);


