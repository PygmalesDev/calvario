package de.uniks.stp24.service;

import de.uniks.stp24.rest.GamesApiService;

import javax.inject.Inject;

public class BrowseGameService {
    @Inject
    GamesApiService GamesApiService;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    PrefService prefService;

    @Inject
    public BrowseGameService() {
    }
}
