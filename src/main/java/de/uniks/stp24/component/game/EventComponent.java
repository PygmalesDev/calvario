package de.uniks.stp24.component.game;

import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.Objects;

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
    ImageView skullImage;
    @FXML
    Button closeEvent;

    @FXML
    StackPane parent;

    @Inject
    EventService eventService;
    @Inject
    TimerService timerService;

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

    }

    @OnDestroy
    public void destroy() {

    }

    public void close() {
        System.out.println("close event");
        parent.setVisible(false);
    }

    public void setParent(StackPane parent) {
        this.parent = parent;
    }
}
