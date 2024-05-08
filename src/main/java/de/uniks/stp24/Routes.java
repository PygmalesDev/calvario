package de.uniks.stp24;

import de.uniks.stp24.controllers.*;
import org.fulib.fx.annotation.Route;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.security.auth.login.LoginContext;

@Singleton
public class Routes {
    @Route("load")
    @Inject
    Provider<LoadController> load;

    @Route("login")
    @Inject
    Provider<LoginController> login;

    @Route("signup")
    @Inject
    Provider<SignUpController> signup;

    @Route("browseGames")
    @Inject
    
    @Route("browseGames")
    @Inject
    Provider<BrowseGameController> browseGames;
    Provider<LicensesController> licenses;
    @Route("holder")
    @Inject
    Provider<PlaceHolderController> holder;
    @Route("editgame")
    @Inject
    Provider<EditGameController> editgame;

    @Route("createGameController")
    @Inject
    Provider<CreateGameController> createGameController;
    Provider<BrowseGameController> browseGames;

    @Route("autoLogin")
    @Inject
    Provider<AutoLoginController> autoLogin;

    @Inject
    public Routes() {
    }
}
