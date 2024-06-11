package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.dto.UpdateSpeedDto;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService {

    protected PropertyChangeSupport listeners;
    public static final String PROPERTY_COUNTDOWN = "countdown";
    public static final String PROPERTY_SPEED = "speed";
    public static final String PROPERTY_SEASON = "season";

    @Inject
    GameStatus gameStatus;
    @Inject
    GamesApiService gamesApiService;

    Timer timer = new Timer();
    final int TIME = 60;
    int countdown = TIME;
    int season;
    int speed;
    private volatile boolean isRunning = false;


    @Inject
    public TimerService() {

    }

    public Observable<UpdateGameResultDto> setSpeed(String gamesid, int speed) {
        return gamesApiService
                .editSpeed(gamesid, new UpdateSpeedDto(speed))
                .doOnNext(updateGameResultDto -> {
                    // After changing the speed,
                    // the local countdown till next season will be updated
                    setSpeedLocal(updateGameResultDto.speed());
                    System.out.println("Speed changed to: " + updateGameResultDto.speed());
                });
    }

    public void start() {

        if (speed == 0) {
            return;
        }

        isRunning = true;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }
                if (countdown > 0) {
                    setCountdown(countdown - 1);
                }
                // if countdown == 0 -> Wait for Server response to call reset() Method
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000 / speed);
    }

    public void stop() {
        isRunning = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void resume() {
        isRunning = true;
        timer = new Timer();
        start();
    }

    public void reset() {
        setCountdown(TIME);
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int value) {
        int oldValue;
        if (value == this.season) {
            return;
        }
        oldValue = this.season;
        this.season = value;
        this.firePropertyChange(PROPERTY_SEASON, oldValue, value);
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int value) {
        int oldValue;
        if (value == this.countdown) {
            return;
        }
        oldValue = this.countdown;
        this.countdown = value;
        this.firePropertyChange(PROPERTY_COUNTDOWN, oldValue, value);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (this.listeners != null) {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public PropertyChangeSupport listeners() {
        if (this.listeners == null) {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeedLocal(int value) {
        int oldValue;
        if (value == this.speed || value == 0) {
            return;
        }
        oldValue = this.speed;
        this.speed = value;

        stop();
        resume();

        this.firePropertyChange(PROPERTY_COUNTDOWN, oldValue, value);
    }

    public boolean isRunning() {
        return isRunning;
    }

}
