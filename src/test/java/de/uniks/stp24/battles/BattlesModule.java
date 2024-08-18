package de.uniks.stp24.battles;

import de.uniks.stp24.appTestModules.IngameModule;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BattlesModule extends IngameModule {
    @Override
    protected void initializeApiMocks() {
        super.initializeApiMocks();

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));

    }
}
