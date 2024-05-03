package de.uniks.stp24;

import de.uniks.stp24.controllers.EnterGameController;
import org.fulib.fx.annotation.Route;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Routes {
    /* TODO add routes
    example:
    @Route("login")
    @Inject
    Provider<LoginController> login;
     */

    @Route("entergame")
    @Inject
    Provider<EnterGameController> entergame;

    @Inject
    public Routes() {
    }
}
