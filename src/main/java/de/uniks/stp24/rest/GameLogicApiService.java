package de.uniks.stp24.rest;

import de.uniks.stp24.dto.ExplainedVariableDTO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public interface GameLogicApiService {

    @GET("games/{game}/empires/{empire}/variables/{variable}")
    Observable<ExplainedVariableDTO> getVariablesExplanations(@Path("empire") String empireID, @Path("variable") String variable);

}
