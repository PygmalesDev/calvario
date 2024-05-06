package de.uniks.stp24;

import de.uniks.stp24.component.PauseMenuComponent;
import de.uniks.stp24.controllers.*;
import org.fulib.fx.annotation.Route;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Routes {
    @Route("game")
    @Inject
    Provider<InGameController> game;

    @Route("pause")
    @Inject
    Provider<PauseMenuComponent> pause;

    @Inject
    public Routes() {
    }
}