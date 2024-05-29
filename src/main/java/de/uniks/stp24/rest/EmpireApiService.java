package de.uniks.stp24.rest;

import de.uniks.stp24.dto.ResourceDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Resource;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public interface EmpireApiService {
    @GET("presets/resources")
    Observable<List<ResourceDto>> getResources();
}
