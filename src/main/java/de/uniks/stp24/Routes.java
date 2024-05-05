package de.uniks.stp24;

import dagger.internal.Provider;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.controllers.PauseController;
import org.fulib.fx.annotation.Route;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Routes {
    @Route("game")
    @Inject
    Provider<InGameController> inGameController;

    @Route("pause")
    @Inject
    Provider<PauseController> pauseController;

    @Inject
    public Routes() {
    }
}
