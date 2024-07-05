package de.uniks.stp24.rest;

import de.uniks.stp24.model.Trait;
import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.DistrictPresets;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.model.TechnologyExtended;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public interface PresetsApiService {
    @GET("presets/system-upgrades")
    Observable<SystemUpgrades> getSystemUpgrades();

    @GET("presets/buildings")
    Observable<ArrayList<BuildingPresets>> getBuildingPresets();

    @GET("presets/districts")
    Observable<ArrayList<DistrictPresets>> getDistrictPresets();

    @GET("presets/traits")
    Observable<Trait[]> getTraitsPreset();

    @GET("presets/technologies")
    Observable<ArrayList<TechnologyExtended>> getTechnologies();

    @GET("presets/technologies/{id}")
    Observable<TechnologyExtended> getTechnology(@Path("id") String id);
}
