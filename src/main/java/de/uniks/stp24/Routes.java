package de.uniks.stp24.controllers;

import javax.inject.Inject;
import javax.inject.Singleton;
import de.uniks.stp24.controllers.*;
import org.fulib.fx.annotation.Route;




@Singleton
public class Routes {

    @Route("holder")
    @Inject
    Provider<PlaceHolderController> holder;

    
    @Inject
    public Routes() {
    }
}
