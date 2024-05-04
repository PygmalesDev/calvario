package de.uniks.stp24.rest;

import de.uniks.stp24.dto.CreateGameDto;
import de.uniks.stp24.dto.CreateGameResultDto;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GamesApiService {
    @POST("games")
    Observable<UpdateGameResultDto> editGame(@Body UpdateGameDto dto);


    @POST("games")
    Observable<CreateGameResultDto> createGame(@Body CreateGameDto dto);
}
