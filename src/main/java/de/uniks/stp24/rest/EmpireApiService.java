package de.uniks.stp24.rest;

import de.uniks.stp24.dto.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public interface EmpireApiService {
    @GET("games/{game}/empires")
    Observable<ReadEmpireDto[]> getEmpires(@Path("game") String gameID);

    @GET("games/{game}/empires/{empire}")
    Observable<EmpireDto> getEmpire(@Path("game") String game, @Path("empire") String empire);

    @PATCH("games/{game}/empires/{empire}")
    Observable<EmpireDto> updateEmpire(@Path("game") String game, @Path("empire") String empire, @Body UpdateEmpireDto updateEmpireDto);

    @PATCH("games/{game}/empires/{empire}")
    Observable<EmpireDto> setEffect(@Path("game") String gameId, @Path("empire") String empireId, @Body EffectSourceParentDto effect);

    @GET("presets/resources")
    Observable<List<ResourceDto>> getResources();

    @GET("games/{game}/empires/{empire}/aggregates/resources.periodic")
    Observable<AggregateResultDto> getResourceAggregates(@Path("game") String game, @Path("empire") String empire);

    @PATCH("games/{game}/empires/{empire}")
    Observable<UpdateEmpireMarketDto> updateEmpireMarket(@Path("game") String game, @Path("empire") String empire, @Body UpdateEmpireMarketDto updateEmpireMarketDto);

    @PATCH("games/{game}/empires/{empire}")
    Observable<UpdateEmpireMarketDto> saveSeasonalComponents(@Path("game") String game, @Path("empire") String empire, @Body SeasonalTradeDto seasonalTradeDto);

    @GET("games/{game}/empires/{empire}")
    Observable<SeasonalTradeDto> getSeasonalTrades(@Path("game") String game, @Path("empire") String empire);


}
