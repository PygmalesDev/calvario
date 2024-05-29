package de.uniks.stp24.service.menu;

import de.uniks.stp24.model.GameStatus;

import javax.inject.Inject;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService {
    @Inject
    GameStatus gameStatus;
    Timer timer = new Timer();
    String time;
    int countdown = 5 * 60;
    int speed = 1;

    @Inject
    public TimerService() {

    }

    public void start() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (countdown > 0) {
                    String suffix = countdown % 60 < 10 ? "0" : "";
                    time = (countdown / 60) + ":" + suffix + (countdown % 60);
                    countdown--;
                } else {
                    countdown = (5 * 60);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000 / speed);
    }

    public void stop() {
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

    public int getTimer() { return countdown;}

}
