package de.uniks.stp24.service.menu;

import de.uniks.stp24.model.GameStatus;
import javax.inject.Inject;
import java.beans.PropertyChangeSupport;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService {

    protected PropertyChangeSupport listeners;
    public static final String PROPERTY_COUNTDOWN = "countdown";

    @Inject
    GameStatus gameStatus;
    Timer timer = new Timer();
    String time;
    int countdown = 5 * 60;
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
                    String suffix = countdown % 60 < 10 ? "0" : "";
                    time = (countdown / 60) + ":" + suffix + (countdown % 60);
                    setCountdown(countdown - speed);
                } else {
                    setCountdown(5 * 60);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void stop() {
        isRunning = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void resume() {
        timer = new Timer();
        start();
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        stop();
        resume();
    }

    public int getCountdown() {
        return countdown;
    }

    public TimerService setCountdown(int value) {
        int oldValue = 0;
        if (value == this.countdown) {
            return this;
        }
        oldValue = this.countdown;
        this.countdown = value;
        this.firePropertyChange(PROPERTY_COUNTDOWN, oldValue, value);
        return this;
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


    public void setShowFlags(boolean showFlags) {
        this.showFlags = showFlags;
    }

    public boolean getShowFlags() {
        return showFlags;
    }
}
