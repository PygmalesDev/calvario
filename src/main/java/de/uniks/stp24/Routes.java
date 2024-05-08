package de.uniks.stp24;

import de.uniks.stp24.controllers.LobbyController;
import org.fulib.fx.annotation.Route;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Routes {
    @Route("lobby")
    @Inject
    Provider<LobbyController> lobby;

    @Inject
    public Routes() {
    }
}
