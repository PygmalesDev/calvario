package de.uniks.stp24.component;

import de.uniks.stp24.App;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.TimerService;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;


@Component(view = "Clock.fxml")
public class ClockComponent extends AnchorPane {

    @FXML
    ImageView spectatorImage;
    @FXML
    VBox clockVBox;
    @FXML
    ToggleButton flagToggle;
    @FXML
    ImageView randomEventImage;
    @FXML
    Label remainingSeasonsLabel;
    @FXML
    AnchorPane anchor;
    @FXML
    Button x1Button;
    @FXML
    Button x2Button;
    @FXML
    Button x3Button;
    @FXML
    ToggleButton pauseClockButton;
    @FXML
    Label seasonLabel;
    @FXML
    Label countdownLabel;

    @Param("gameid")
    String gameId;

    @Inject
    App app;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    TimerService timerService;
    @Inject
    InGameService inGameService;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    GameMembersApiService gameMembersApiService;
    @Inject
    Subscriber subscriber;

    @Inject
    public ClockComponent() {

    }

    @OnInit
    public void init() {

        PropertyChangeListener callHandleTimeChanged = this::handleTimeChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_COUNTDOWN, callHandleTimeChanged);

        PropertyChangeListener callHandleSpeedChanged = this::handleSpeedChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SPEED, callHandleSpeedChanged);

        PropertyChangeListener callHandleSeasonChanged = this::handleSeasonChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SEASON, callHandleSeasonChanged);
    }

    @OnRender
    public void render() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/clock.css")).toExternalForm();
        this.getStylesheets().add(css);

        // adding spectator sign
        if (!tokenStorage.isSpectator()) {
            spectatorImage.setImage(new Image("de/uniks/stp24/gameIcons/spectatorSign.png"));
        }

        subscriber.subscribe(gamesApiService.getGame(gameId),
                // Set Clock and Season for the current Game
                game -> {
                    timerService.setSpeedLocal(game.speed());
                    timerService.setSeason(game.period());
                });

        timerService.start();
        seasonLabel.setText(timerService.getSeason() + "");
        countdownLabel.setText(translateCountdown(timerService.getCountdown()));

        subscriber.subscribe(gamesApiService.getGame(gameId),
            result -> {
            // Only owner of the game can change the speed
                if (!(Objects.equals(result.owner(), tokenStorage.getUserId()))) {
                    x1Button.setVisible(false);
                    x2Button.setVisible(false);
                    x3Button.setVisible(false);
                    pauseClockButton.setVisible(false);
                }
            });

        // Dummy data for special Event
        randomEventImage.setImage(new Image("de/uniks/stp24/assets/events/goodEvent.png"));
        remainingSeasonsLabel.setText("3");
    }

    @OnDestroy
    public void destroy() {
        timerService.listeners().removePropertyChangeListener(TimerService.PROPERTY_COUNTDOWN, this::handleTimeChanged);
        timerService.listeners().removePropertyChangeListener(TimerService.PROPERTY_SPEED, this::handleSpeedChanged);
        timerService.listeners().removePropertyChangeListener(TimerService.PROPERTY_SEASON, this::handleSeasonChanged);
        timerService.stop();

        if (subscriber != null) {
            subscriber.dispose();
        }
    }

    public void showFlags() {
        timerService.setShowFlags(!timerService.getShowFlags());
    }

    public void pauseClock() {
        if (timerService.isRunning()) {
            subscriber.subscribe(timerService.setSpeed(gameId, 0));
            timerService.stop();
        } else {
            subscriber.subscribe(timerService.setSpeed(gameId, timerService.getSpeed()));
            timerService.resume();
        }
    }

    public void x3() {
        changingSpeed(3);
    }

    public void x2() {
        changingSpeed(2);
    }

    public void x1() {
        changingSpeed(1);
    }

    private String translateCountdown(int countdown) {
        String suffix = (countdown % 60) < 10 ? "0" : "";
        return (countdown / 60) + ":" + suffix + (countdown % 60);
    }

    public void changingSpeed(int speed) {
        if (!timerService.isRunning()) {
            timerService.resume();
        }
        if (timerService.getSpeed() != speed) {
            subscriber.subscribe(timerService.setSpeed(gameId, speed));
        }
    }

    private void handleSeasonChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int season = (int) propertyChangeEvent.getNewValue();
            Platform.runLater(() -> seasonLabel.setText(String.valueOf(season)));
        }
    }

    private void handleSpeedChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int speed = (int) propertyChangeEvent.getNewValue();
            timerService.setSpeedLocal(speed);
        }
    }

    private void handleTimeChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int time = (int) propertyChangeEvent.getNewValue();
            Platform.runLater(() -> countdownLabel.setText(translateCountdown(time)));
        }
    }
}
