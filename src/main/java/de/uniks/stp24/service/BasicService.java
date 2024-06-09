package de.uniks.stp24.service;

import de.uniks.stp24.App;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BasicService {
    @Inject
    protected App app;
    @Inject
    protected TokenStorage tokenStorage;
    @Inject
    protected Subscriber subscriber;
    @Inject
    protected ErrorService errorService;
}
