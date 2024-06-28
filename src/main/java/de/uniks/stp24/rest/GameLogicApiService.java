package de.uniks.stp24.rest;

import de.uniks.stp24.dto.ExplainedVariableDTO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.inject.Singleton;

@Singleton
public interface GameLogicApiService {

    @GET("games/{game}/empires/{empire}/variables")
    Observable<ExplainedVariableDTO[]> getVariablesExplanations(@Path("game") String gameID, @Path("empire") String empireID);
}
