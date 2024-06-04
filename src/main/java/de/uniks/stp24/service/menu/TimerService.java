package de.uniks.stp24.service.menu;

import de.uniks.stp24.model.GameStatus;
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
    Timer timer = new Timer();
    final int TIME = 60;
    int countdown = TIME;
    int season = 0;
    int speed = 1;
    boolean showFlags = false;

    private volatile boolean isRunning = false;

    @Inject
    public TimerService() {

    }

    public void start() {
        isRunning = true;

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }
                if (countdown > 0) {
                    setCountdown(countdown - 1);
                } else {
                    setSeason(season + 1);
                    setCountdown(TIME);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000 / speed);
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

    public void setSpeed(int value) {
        int oldValue;
        if (value == this.speed) {
            return;
        }
        oldValue = this.speed;
        this.speed = value;

        stop();
        resume();

        this.firePropertyChange(PROPERTY_COUNTDOWN, oldValue, value);
    }


    public void setShowFlags(boolean showFlags) {
        this.showFlags = showFlags;
    }

    public boolean getShowFlags() {
        return showFlags;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
