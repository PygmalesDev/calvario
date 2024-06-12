package de.uniks.stp24.component.game;

import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "Event.fxml")
public class EventComponent extends AnchorPane {

    @FXML
    AnchorPane anchor;
    @FXML
    Text eventDescription;
    @FXML
    Text eventName;
    @FXML
    ImageView eventImage;
    @FXML
    Button closeEvent;

    @FXML
    StackPane parent;

    @Inject
    EventService eventService;
    @Inject
    TimerService timerService;

    EffectSourceDto event;

    @Inject
    @Resource
    public ResourceBundle resourcesBundle;
    @Inject
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    ImageCache imageCache = new ImageCache();

    @Inject
    public EventComponent() {

    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/style/event.css")).toExternalForm();
        this.getStylesheets().add(css);

        setEventImages(eventService.getEvent());

    }

    private @NotNull String convert(@NotNull String id) {
        String[] word = id.split("_");
        for (int i = 1; i < word.length; i++) {
            word[i] = word[i].substring(0, 1).toUpperCase() + word[i].substring(1);
        }
        return String.join("", word);
    }

    private void setEventImages(@NotNull EffectSourceDto event) {

        String id = convert(event.id());

        eventImage.setImage(imageCache.get("icons/events/"+ id + "Event.png"));
        System.out.println("event." + id + ".description");
        eventName.setText(resources.getString("event." + id + ".name"));
        eventDescription.setText(resources.getString("event." + id + ".description"));

    }

    @OnDestroy
    public void destroy() {

    }

    public void close() {
        System.out.println("close event");
        parent.setVisible(false);
    }

    public void show() {
        parent.setVisible(true);
    }

    public void setParent(StackPane parent) {
        this.parent = parent;
    }
}
