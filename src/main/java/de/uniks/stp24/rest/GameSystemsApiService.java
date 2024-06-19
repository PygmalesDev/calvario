package de.uniks.stp24.rest;

import de.uniks.stp24.dto.SystemDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

public interface GameSystemsApiService {
    @GET("games/{game}/systems")
    Observable<SystemDto[]> getSystems(@Path("game") String gameID);

    @GET("games/{game}/systems/{id})")
    Observable<SystemDto> getOneSystem(@Path("game") String gameID, @Path("id") String systemID);
}
