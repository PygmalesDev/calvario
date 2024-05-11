package de.uniks.stp24;

import de.uniks.stp24.component.WarningScreenComponent;
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
    
    @Route("autoLogin")
    @Inject
    Provider<AutoLoginController> autoLogin;

    @Route("signup")
    @Inject
    Provider<SignUpController> signup;
    
    @Route("browseGames")
    @Inject
    Provider<BrowseGameController> browseGames;
    @Route("licenses")
    @Inject
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

    @Route("editAcc")
    @Inject
    Provider<EditAccController> editAcc;

    @Route("warningScreen")
    @Inject
    Provider<WarningScreenComponent> warningScreen;

    @Inject
    public Routes() {
    }
}