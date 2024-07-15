package de.uniks.stp24.rest;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.inject.Singleton;

@Singleton
public interface GameLogicApiService {

    @GET("games/{game}/empires/{empire}/variables/{variable}")
    Observable<ExplainedVariableDTO> getVariablesExplanations(@Path("empire") String empireID, @Path("variable") String variable);

    @GET("games/{game}/empires/{empire}/aggregates/{aggregate}/technologies/{id}")
    Observable<AggregateResultDto> getTechnologyCostAndTime(@Path("empire") String empireID, @Path("aggregate") String aggregate, @Path("id") String techID);

}
