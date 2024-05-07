package de.uniks.stp24.service;

import de.uniks.stp24.model.Game;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InGameService {

    @Inject
    Game game;

    @Inject
    public InGameService() {

    }

    public void setPaused(Boolean isPaused) {
       game.setPaused(isPaused);
    }

    public Boolean getPaused() {
        return game.getPaused();
    }

    public Game getGame() {
        return game;
    }

    public void setShowSettings(boolean show) {
        game.setShowSettings(show);
    }
}
