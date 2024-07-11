package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.EffectSourceParentDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "Event.fxml")
public class EventComponent extends AnchorPane {

    @FXML
    TextFlow descriptionTextFlow;
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

    String gameId;

    ImageCache imageCache = new ImageCache();
    private String lastUpdate = "";

    @Inject
    public EventComponent() {

    }

    @OnInit
    public void init() {

        PropertyChangeListener callHandleEventChanged = this::handleEventChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_EVENT, callHandleEventChanged);

        PropertyChangeListener callHandleShowEventChanged = this::handleShowEventChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SHOWEVENT, callHandleShowEventChanged);

        createUpdateSeasonsListener();

    }

    @OnRender
    public void render() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/style/event.css")).toExternalForm();
        this.getStylesheets().add(css);

        gameId = tokenStorage.getGameId();
    }

    @OnDestroy
    public void destroy() {
        eventService.listeners().removePropertyChangeListener(EventService.PROPERTY_EVENT, this::handleEventChanged);
        timerService.listeners().removePropertyChangeListener(TimerService.PROPERTY_SHOWEVENT, this::handleShowEventChanged);
    }

    public void createUpdateSeasonsListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {
                    if (!Objects.equals(lastUpdate, event.data().updatedAt())) {
                        eventService.setNextEventTimer(eventService.getNextEventTimer() - 1);
                        if (eventService.getEvent() != null) {
                            subscriber.subscribe(eventService.sendEffect(),
                                    result -> {
                                    },
                                    error -> System.out.println("Error beim Senden von Effect: " + error)
                            );
                            setRandomEventInfos(eventService.getEvent());
                            show();
                        }
                        lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("Error bei Season: " + error.getMessage())
        );
    }

    private void handleEventChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(eventService.getEvent())) {
            setRandomEventInfos(eventService.getEvent());
            show();
        }
    }

    private void handleShowEventChanged(PropertyChangeEvent propertyChangeEvent) {
        if (timerService.getShowEvent()) {
            show();
        } else {
            close();
        }
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
        String description = resources.getString("event." + id + ".description");
        TextFlow descriptionTextFlow = createTextFlow(description);
        descriptionTextFlow.getStyleClass().clear();
        this.descriptionTextFlow.getChildren().setAll(descriptionTextFlow.getChildren());
    }

    private TextFlow createTextFlow(String text) {
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().clear();
        String[] lines = text.split("\\s+");

        for (String line : lines) {
            Text textNode = new Text(line + " ");
            textNode.getStyleClass().add("eventDescription");
            if (line.contains("Gain") || line.contains("doubled") ||
                    line.contains("Erhalte") || line.contains("verdoppelt") ||
                    line.matches(".*\\+.*") || line.matches(".*x.*")) {
                textNode.setFill(Color.GREEN);
            } else if (line.contains("Lose") || line.contains("halved") || line.contains("decreased") ||
                    line.contains("Verliere") || line.contains("halbiert") ||
                    line.matches(".*-.*")) {
                textNode.setFill(Color.RED);
            } else {
                textNode.setFill(Color.BLACK);
            }

            textFlow.getChildren().add(textNode);
        }

        return textFlow;
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

    public void close() {
        container.setVisible(false);
        shadow.setVisible(false);
        timerService.setShowEvent(false);
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


}
