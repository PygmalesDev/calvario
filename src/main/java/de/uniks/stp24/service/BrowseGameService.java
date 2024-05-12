package de.uniks.stp24.service;
import dagger.Provides;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;

import javax.inject.Inject;

public class BrowseGameService {
    @Inject
    GamesApiService GamesApiService;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    PrefService prefService;

    private Game game;

    @Inject
    public BrowseGameService() {
    }

    public void handleGameSelection(Game game) {
        this.game = game;

        if (game != null) {
            System.out.println("Selected Game: " + game.name());
        } else {
            System.out.println("No game selected");
        }
    }
}
