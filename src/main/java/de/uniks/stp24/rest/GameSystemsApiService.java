package de.uniks.stp24.rest;

import de.uniks.stp24.dto.CreateSystemsDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.UpdateBuildingDto;
import de.uniks.stp24.dto.UpgradeSystemDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import javax.inject.Singleton;

@Singleton
public interface GameSystemsApiService {

    @GET("games/{game}/systems/{id}")
    Observable<SystemDto> getCertainIsland(@Path("game") String gameID, @Path("id") String islandID);

    @PATCH("games/{game}/systems/{id}")
    Observable<CreateSystemsDto> updateBuildings(@Path("game") String gameID, @Path("id") String ownerID, @Body UpdateBuildingDto dto);

    @PATCH("games/{game}/systems/{id}")
    Observable<CreateSystemsDto> upgradeSystem(@Path("game") String gameID, @Path("id") String ownerID, @Body UpgradeSystemDto dto);

    @GET("games/{game}/systems")
    Observable<SystemDto[]> getSystems(@Path("game") String gameID);

}
