package de.uniks.stp24.rest;

import de.uniks.stp24.model.ExplainedVariable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GameLogicApiService {
    @GET("games/{gameID}/empires/{empireID}/variables")
    Observable<List<ExplainedVariable>> getVariables(@Path("gameID") String gameID, @Path("empireID") String empireID, @Query("variables") List<String> variables);

    @GET("games/{gameID}/empires/{empireID}/variables/{variable}")
    Observable<ExplainedVariable> getVariable(@Path("gameID") String gameID, @Path("empireID") String empireID, @Path("variable") String variable);


}
