package de.uniks.stp24.service;

import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class InGameService {

    @Inject
    TimerService timerService;
    @Inject
    GameStatus gameStatus;
    @Inject
    EventService eventService;

    @Inject
    public InGameService() {

    }

    public void setPaused(Boolean isPaused) {
       gameStatus.setPaused(isPaused);
    }

    public Boolean getPaused() {
        return gameStatus.getPaused();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setShowSettings(boolean show) {
        gameStatus.setShowSettings(show);
    }

    public void setLanguage(int lang) {
        gameStatus.setLanguage(lang);
    }

    public int getLanguage() {
        return gameStatus.getLanguage();
    }

    public void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }

    public void setEventService(EventService eventService) {this.eventService = eventService;}

    public TimerService getTimerService() {
        return timerService;
    }
}
