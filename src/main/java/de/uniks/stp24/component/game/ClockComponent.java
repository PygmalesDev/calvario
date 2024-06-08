package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
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
    ToggleGroup speed;
    @FXML
    RadioButton x1Button;
    @FXML
    RadioButton x2Button;
    @FXML
    RadioButton x3Button;
    @FXML
    RadioButton pauseClockButton;
    @FXML
    Label seasonLabel;
    @FXML
    Label countdownLabel;

    String gameId;

    ImageCache imageCache = new ImageCache();

    @Inject
    TokenStorage tokenStorage;
    @Inject
    TimerService timerService;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;

    @Inject
    public ClockComponent() {

    }

    @OnInit
    public void init() {

        gameId = tokenStorage.getGameId();

        PropertyChangeListener callHandleTimeChanged = this::handleTimeChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_COUNTDOWN, callHandleTimeChanged);

        PropertyChangeListener callHandleSpeedChanged = this::handleSpeedChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SPEED, callHandleSpeedChanged);

        PropertyChangeListener callHandleSeasonChanged = this::handleSeasonChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SEASON, callHandleSeasonChanged);

        createUpdateSeasonListener();
        createUpdateSpeedListener();

    }

    @OnRender
    public void render() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/clock.css")).toExternalForm();
        this.getStylesheets().add(css);

        // adding spectator sign
        if (tokenStorage.isSpectator()) {
            spectatorImage.setImage(imageCache.get("gameIcons/spectatorSign.png"));
        }

        subscriber.subscribe(gamesApiService.getGame(gameId),
                game -> {
                    // Only owner of the game can change the speed
                    if (!(Objects.equals(game.owner(), tokenStorage.getUserId()))) {
                        x1Button.setVisible(false);
                        x2Button.setVisible(false);
                        x3Button.setVisible(false);
                        pauseClockButton.setVisible(false);
                    }
                    // Set Clock and Season for the current Game
                    switch (game.speed()) {
                        case 0:
                            pauseClockButton.setSelected(true);
                            break;
                        case 1:
                            x1Button.setSelected(true);
                            break;
                        case 2:
                            x2Button.setSelected(true);
                            break;
                        case 3:
                            x3Button.setSelected(true);
                            break;
                    }
                    timerService.setSpeedLocal(game.speed());
                    timerService.setSeason(game.period());
                });

        timerService.start();
        seasonLabel.setText(timerService.getSeason() + "");
        countdownLabel.setText(translateCountdown(timerService.getCountdown()));

        // Dummy data for special Event
        randomEventImage.setImage(imageCache.get("assets/events/goodEvent.png"));
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
        timerService.stop();
    }

    /////////////---------------------------------------WS-EventListeners---------------------------------//////////////

    public void createUpdateSeasonListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + gameId + ".ticked", Game.class),
                event -> {
                    System.out.println("Season changed");
                    Game game = event.data();
                    timerService.setSeason(game.period());
                    timerService.reset();
                },
                error -> System.out.println("Error bei Season: " + error.getMessage()));
    }

    public void createUpdateSpeedListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + gameId + ".updated", Game.class),
                event -> {
                    System.out.println("Update Speed");
                    Game game = event.data();
                    timerService.setSpeedLocal(game.speed());
                },
                error -> System.out.println("Error bei Speed: " + error.getMessage()));
    }

    ///////////////--------------------------------------------onAction------------------------------------/////////////

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

    ////////////--------------------------------Auxiliary Methods-----------------------------------------//////////////

    private String translateCountdown(int countdown) {
        String suffix = (countdown % 60) < 10 ? "0" : "";
        return (countdown / 60) + ":" + suffix + (countdown % 60);
    }

    // Set size of seasonLabel in case of long season number
    private void setSeasonLabelSize() {
        if (timerService.getSeason() > 999) {
            seasonLabel.setStyle("-fx-font-size: 15px;");
        }
    }

    public void changingSpeed(int speed) {
        if (!timerService.isRunning()) {
            timerService.resume();
        }
        if (timerService.getSpeed() != speed) {
            subscriber.subscribe(timerService.setSpeed(gameId, speed));
        }
    }

    ////////////--------------------------------PropertyChangeListener------------------------------------//////////////

    private void handleSeasonChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int season = (int) propertyChangeEvent.getNewValue();
            setSeasonLabelSize();
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
