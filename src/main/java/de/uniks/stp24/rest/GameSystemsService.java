package de.uniks.stp24.rest;

import de.uniks.stp24.model.Island;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GameSystemsService {
    @GET("games/{game}/systems")
    Observable<Island[]> getSystems(@Path("game") String gameID);
}
