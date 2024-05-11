package de.uniks.stp24;

import de.uniks.stp24.controllers.LobbyController;
import org.fulib.fx.annotation.Route;

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

    @Route("autoLogin")
    @Inject
    Provider<AutoLoginController> autoLogin;

    @Route("signup")
    @Inject
    Provider<SignUpController> signup;

    @Route("licenses")
    @Inject
    Provider<LicensesController> licenses;

    @Route("holder")
    @Inject
    Provider<PlaceHolderController> holder;

    @Route("editAcc")
    @Inject
    Provider<EditAccController> editAcc;

    @Route("warningScreen")
    @Inject
    Provider<WarningScreenComponent> warningScreen;

    @Route("lobby")
    @Inject
    Provider<LobbyController> lobby;

    @Route("login")
    @Inject
    Provider<LoginController> login;

    @Inject
    public Routes() {
    }
}
