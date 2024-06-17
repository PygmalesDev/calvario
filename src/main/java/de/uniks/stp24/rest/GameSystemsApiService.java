package de.uniks.stp24.rest;

import de.uniks.stp24.dto.CreateSystemsDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.UpdateSystemDto;
import de.uniks.stp24.model.Island;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import javax.inject.Singleton;

@Singleton
public interface GameSystemsApiService {

    @GET("games/{game}/systems/{id}")
    Observable<Island> getCertainIsland(@Path("game") String gameID, @Path("id") String ownerID);

    @PATCH("games/{game}/systems/{id}")
    Observable<CreateSystemsDto> updateIsland(@Path("game") String gameID, @Path("id") String ownerID, @Body UpdateSystemDto dto);

    @GET("games/{game}/systems")
    Observable<SystemDto[]> getSystems(@Path("game") String gameID);

}
