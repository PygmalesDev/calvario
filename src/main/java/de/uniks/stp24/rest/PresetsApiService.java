package de.uniks.stp24.rest;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.SystemUpgrades;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

import javax.inject.Singleton;

@Singleton
public interface PresetsApiService {
    @GET("/presets/system-upgrades")
    Observable<SystemUpgrades> getSystemUpgrades();
}
