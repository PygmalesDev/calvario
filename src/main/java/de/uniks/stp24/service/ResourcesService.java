package de.uniks.stp24.service;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Building;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.Site;
import de.uniks.stp24.rest.GameSystemsApiService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ResourcesService {

    @Inject
    GameSystemsApiService gameSystemsApiService;

    @Inject
    Subscriber subscriber;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public ResourcesService() {

    }


}
