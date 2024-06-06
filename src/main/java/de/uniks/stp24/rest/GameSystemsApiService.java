package de.uniks.stp24.rest;

import de.uniks.stp24.dto.SystemsDto;
import de.uniks.stp24.dto.SystemsResultDto;
import de.uniks.stp24.model.Island;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public interface GameSystemsApiService {

    @GET("games/{game}/systems")
    Observable<List<Island>> getAllIslands(@Path("game") String gameID);

    @GET("games/{game}/systems/{id}")
    Observable<Island> getCertainIsland(@Path("game") String gameID, @Path("id") String islandID);

    @PATCH("games/{game}/systems/{id}")
    Observable<SystemsResultDto> updateIsland(@Path("game") String gameID, @Path("id") String islandID, @Body SystemsDto dto);
}
