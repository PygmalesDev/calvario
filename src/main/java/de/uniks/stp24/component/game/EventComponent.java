package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.EffectSourceParentDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "Event.fxml")
public class EventComponent extends AnchorPane {
    @FXML
    AnchorPane anchor;
    @FXML
    ScrollPane descriptionScrollPane;
    @FXML
    Text eventDescription;
    @FXML
    Text eventName;
    @FXML
    public ImageView eventImage;
    @FXML
    Button closeEvent;

    @FXML
    Pane shadow;
    @FXML
    StackPane container;

    @Inject
    App app;
    @Inject
    EventService eventService;
    @Inject
    TimerService timerService;

    ClockComponent clockComponent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;
    @Inject
    EventListener eventListener;
    @Inject
    Subscriber subscriber;
    @Inject
    public TokenStorage tokenStorage;

    Pane gameBackground;

    String gameId;

    ImageCache imageCache = new ImageCache();
    private String lastUpdate = "";


    ColorAdjust fadeAdjust;
    ColorAdjust unfadeAdjust;
    ColorAdjust brightenAdjsut;
    Timeline nightTimeLine;
    Timeline dayTimeLine;
    boolean isDay = true;

    @Inject
    public EventComponent() {

    }

    @OnInit
    public void init() {
        PropertyChangeListener callHandleEventChanged = this::handleEventChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_EVENT, callHandleEventChanged);

        createUpdateSeasonsListener();
    }


    public void createUpdateSeasonsListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {
                    if (!Objects.equals(lastUpdate, event.data().updatedAt())) {
                        eventService.setNextEventTimer(eventService.getNextEventTimer()-1);
                        // todo delete souts
                        System.out.println("here event");
                        EffectSourceParentDto activeEvent = eventService.getEvent();
                        System.out.println(activeEvent);
                        if (activeEvent != null) {
                            // todo delete souts
                            System.out.println("here event 2");
                            if (activeEvent.effects()[0].eventType().equals("misty") && isDay) {
                                changeToNight();
                            }
                            subscriber.subscribe(eventService.sendEffect(),
                                    result -> {},
                                    error -> System.out.println("Error beim Senden von Effect: " + error));

                            setRandomEventInfos(activeEvent);
                            show();
                        } else {
                            if (!isDay) {
                                changeToDay();
                            }
                        }
                        lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("Error bei Season: " + error.getMessage()));
    }

    private void handleEventChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(eventService.getEvent())) {
            setRandomEventInfos(eventService.getEvent());
            show();
        }
    }


    @OnRender
    public void render() {
        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/style/event.css")).toExternalForm();
        this.getStylesheets().add(css);

        gameId = tokenStorage.getGameId();

        fadeAdjust = new ColorAdjust();
        fadeAdjust.setBrightness(0);

        unfadeAdjust = new ColorAdjust();
        unfadeAdjust.setBrightness(0);

        brightenAdjsut = new ColorAdjust();
        brightenAdjsut.setBrightness(0);

        nightTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(fadeAdjust.brightnessProperty(), fadeAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(7), new KeyValue(fadeAdjust.brightnessProperty(), -0.8, Interpolator.LINEAR)
                ));
        nightTimeLine.setCycleCount(1);
        nightTimeLine.setAutoReverse(false);

        dayTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(unfadeAdjust.brightnessProperty(), unfadeAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(3), new KeyValue(unfadeAdjust.brightnessProperty(), 0.3, Interpolator.LINEAR)
                ));
        dayTimeLine.setCycleCount(1);
        dayTimeLine.setAutoReverse(false);
    }

    // changes String to camelCase
    private @NotNull String convert(@NotNull String id) {
        String[] word = id.split("_");
        for (int i = 1; i < word.length; i++) {
            word[i] = word[i].substring(0, 1).toUpperCase() + word[i].substring(1);
        }
        return String.join("", word);
    }

    public void setRandomEventInfos(@NotNull EffectSourceParentDto event) {
        String id = convert(event.effects()[0].id());

        checkSize(id);

        eventImage.setImage(imageCache.get("icons/events/" + id + "Event.png"));
        eventName.setText(resources.getString("event." + id + ".name"));
        eventDescription.setText(resources.getString("event." + id + ".description"));
    }

    // reduce size of eventName if it's too long
    private void checkSize(@NotNull String id) {
        if (id.length() - 15 > 0) {
            int num = id.length() % 15;
            int size = 32 - (num / 2);
            eventName.setStyle("-fx-font-size: " + size);
            eventName.setTranslateY(eventName.getTranslateY() + num - 3);
            descriptionScrollPane.setTranslateY(descriptionScrollPane.getTranslateY() + num - 3);
        }

    }

    @OnDestroy
    public void destroy() {

    }

    public void close() {
        container.setVisible(false);
        shadow.setVisible(false);
    }

    public void show() {
        container.setVisible(true);
        shadow.setVisible(true);
        shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
    }

    public void setParent(Pane shadow, StackPane container) {
        this.shadow = shadow;
        this.container = container;
    }

    public void setClockComponent(ClockComponent clockComponent) {
        this.clockComponent = clockComponent;
    }

    public void changeToNight() {
        nightTimeLine.stop();

        gameBackground.setEffect(fadeAdjust);

        nightTimeLine.setOnFinished(event -> {
            gameBackground.setEffect(brightenAdjsut);
            gameBackground.setStyle(NIGHT);
            isDay = false;
        });

        nightTimeLine.play();
    }

    public void changeToDay() {
        dayTimeLine.stop();

        gameBackground.setEffect(unfadeAdjust);

        dayTimeLine.setOnFinished(event -> {
            gameBackground.setEffect(brightenAdjsut);
            gameBackground.setStyle(DAY);
            isDay = true;
        });

        dayTimeLine.play();
    }

    public void setBackground(Pane gameBackground) {
        this.gameBackground = gameBackground;
    }
}
