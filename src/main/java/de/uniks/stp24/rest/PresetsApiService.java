package de.uniks.stp24.rest;

import de.uniks.stp24.model.SystemUpgrades;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public interface PresetsApiService {
    @GET("presets/system-upgrades")
    Observable<SystemUpgrades> getSystemUpgrades();

    @GET("presets/variables")
    Observable<Map<String, Integer>> getVariablesPresets();
}
