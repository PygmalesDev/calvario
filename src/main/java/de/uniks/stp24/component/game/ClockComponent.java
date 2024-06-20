package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

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
    public ImageView randomEventImage;
    @FXML
    public Label remainingSeasonsLabel;
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
    EventService eventService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public TimerService timerService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public GamesApiService gamesApiService;
    @Inject
    public Subscriber subscriber;
    @Inject
    EventListener eventListener;
    private int lastUpdateSeason = -1;
    private String lastUpdateSpeed = "";
    @Inject
    EventComponent eventComponent;

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

        PropertyChangeListener callHandleRemainingSeasons = this::handleRemainingSeasonChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_REMAININGSEASONS, callHandleRemainingSeasons);

        PropertyChangeListener callHandleEventChanged = this::handleEventChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_EVENT, callHandleEventChanged);

        createUpdateSeasonListener();
        createUpdateSpeedListener();

    }

    @OnRender
    public void render() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/style/clock.css")).toExternalForm();
        this.getStylesheets().add(css);

        // adding spectator sign
        if (tokenStorage.isSpectator()) {
            spectatorImage.setImage(imageCache.get("icons/spectatorSign.png"));
        }

        subscriber.subscribe(gamesApiService.getGame(tokenStorage.getGameId()),
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
                }, System.out::println);

        setSeasonLabelSize();

        timerService.start();
        seasonLabel.setText(timerService.getSeason() + "");
        countdownLabel.setText(translateCountdown(timerService.getCountdown()));

        remainingSeasonsLabel.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        eventService.listeners().removePropertyChangeListener(EventService.PROPERTY_EVENT, this::handleEventChanged);
        eventService.listeners().removePropertyChangeListener(EventService.PROPERTY_REMAININGSEASONS, this::handleRemainingSeasonChanged);
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
                    if (!(lastUpdateSeason == event.data().period())) {
                        Game game = event.data();
                        timerService.setSeason(game.period());
                        timerService.reset();
                        lastUpdateSeason = event.data().period();
                    }
                },
                error -> System.out.println("Error on Season: " + error.getMessage()));
    }

    public void createUpdateSpeedListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + gameId + ".updated", Game.class),
                event -> {
                    if (!lastUpdateSpeed.equals(event.data().updatedAt())) {
                        Game game = event.data();
                        timerService.setSpeedLocal(game.speed());
                        lastUpdateSpeed = event.data().updatedAt();
                    }
                },
                error -> System.out.println("Error on speed: " + error.getMessage()));
    }

    ///////////////--------------------------------------------onAction------------------------------------/////////////

    public void showFlags() {
        islandsService.setFlag(flagToggle.isSelected());
    }

    @OnKey(code = KeyCode.H, shift = true)
    public void setSelected() {
        flagToggle.setSelected(!flagToggle.isSelected());
        showFlags();
    }

    public void pauseClock() {
        if (timerService.isRunning()) {
            subscriber.subscribe(timerService.setSpeed(gameId, 0),
                    result -> {},
                    error -> System.out.println("Error on pause: " + error));
            timerService.stop();
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

    @Contract(pure = true)
    private @NotNull String translateCountdown(int countdown) {
        String suffix = (countdown % 60) < 10 ? "0" : "";
        return (countdown / 60) + ":" + suffix + (countdown % 60);
    }

    // Set size of seasonLabel in case of long season number
    private void setSeasonLabelSize() {
        if (timerService.getSeason() > 999) {
            seasonLabel.setStyle("-fx-font-size: 15px;");
            countdownLabel.setStyle("-fx-font-size: 13px");
            seasonLabel.setTranslateY(seasonLabel.getTranslateY() + 3);
            countdownLabel.setTranslateY(countdownLabel.getTranslateY() + 3);
        }
    }

    public void changingSpeed(int speed) {
        if (!timerService.isRunning()) {
            timerService.resume();
        }
        subscriber.subscribe(timerService.setSpeed(gameId, speed),
                result -> {},
                error -> System.out.println("Error when changing speed: " + error));
    }

    ////////////--------------------------------PropertyChangeListener--------------------------------------////////////

    private void handleEventChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {

        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            randomEventImage.setVisible(true);
            remainingSeasonsLabel.setVisible(true);

            if (Objects.equals(eventService.getEvent().effects()[0].eventType(), "bad")) {
                randomEventImage.setImage(imageCache.get("assets/events/badEvent.png"));
            } else {
                randomEventImage.setImage(imageCache.get("assets/events/goodEvent.png"));
            }
        }
    }

    private void handleRemainingSeasonChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int remainingSeasons = (int) propertyChangeEvent.getNewValue();
            if (remainingSeasons == 0) {

                // Delete event on Server
                eventService.setEvent(null);
                subscriber.subscribe(eventService.sendEffect(),
                        result -> {},
                        error -> {}
                );

                randomEventImage.setVisible(false);
                remainingSeasonsLabel.setVisible(false);
            } else {
                Platform.runLater(() -> remainingSeasonsLabel.setText(String.valueOf(remainingSeasons)));
            }
        }
    }

    private void handleSeasonChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int season = (int) propertyChangeEvent.getNewValue();
            setSeasonLabelSize();
            Platform.runLater(() -> seasonLabel.setText(String.valueOf(season)));
        }
    }

    private void handleSpeedChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int speed = (int) propertyChangeEvent.getNewValue();
            timerService.setSpeedLocal(speed);
        }
    }

    private void handleTimeChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int time = (int) propertyChangeEvent.getNewValue();
            Platform.runLater(() -> countdownLabel.setText(translateCountdown(time)));
        }
    }
}