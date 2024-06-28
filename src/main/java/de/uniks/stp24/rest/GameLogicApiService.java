package de.uniks.stp24.rest;

import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.VariableDTO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.inject.Singleton;

@Singleton
public interface GameLogicApiService {

    @GET("games/{game}/empires/{empire}/variables")
    Observable<VariableDTO[]> getVariables(@Path("game") String gameID, @Path("empire") String empireID);
}
