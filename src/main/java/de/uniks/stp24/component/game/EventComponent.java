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

    private void setEventImages(EffectSourceDto event) {
        eventName.setText(event.id());
        switch (event.id()) {
            case "abundance":
                eventImage.setImage(imageCache.get("icons/events/abundanceEvent.png"));
                eventDescription.setText(resources.getString("event.abundance.description"));
                eventName.setText(resources.getString("event.abundance.name"));
                break;
            case "black_spot":
                eventImage.setImage(imageCache.get("icons/events/blackSpotEvent.png"));
                eventDescription.setText(resources.getString("event.blackSpot.description"));
                eventName.setText(resources.getString("event.blackSpot.name"));
                break;
            case "crapulence":
                eventImage.setImage(imageCache.get("icons/events/crapulenceEvent.png"));
                eventDescription.setText(resources.getString("event.crapulence.description"));
                eventName.setText(resources.getString("event.crapulence.name"));
                break;
            case "dutchman":
                eventImage.setImage(imageCache.get("icons/events/dutchmanEvent.png"));
                eventDescription.setText(resources.getString("event.dutchman.description"));
                eventName.setText(resources.getString("event.dutchman.name"));
                break;
            case "equiv_ex":
                eventImage.setImage(imageCache.get("icons/events/equivExEvent.png"));
                eventDescription.setText(resources.getString("event.equivEx.description"));
                eventName.setText(resources.getString("event.equivEx.name"));
                break;
            case "fools_gold":
                eventImage.setImage(imageCache.get("icons/events/foolsGoldEvent.png"));
                eventDescription.setText(resources.getString("event.foolsGold.description"));
                eventName.setText(resources.getString("event.foolsGold.name"));
                break;
            case "grand_expedition":
                eventImage.setImage(imageCache.get("icons/events/grandExpeditionEvent.png"));
                eventDescription.setText(resources.getString("event.grandExpedition.description"));
                eventName.setText(resources.getString("event.grandExpedition.name"));
                break;
            case "pestilence":
                eventImage.setImage(imageCache.get("icons/events/pestilenceEvent.png"));
                eventDescription.setText(resources.getString("event.pestilence.description"));
                eventName.setText(resources.getString("event.pestilence.name"));
                break;
            case "reckoning":
                eventImage.setImage(imageCache.get("icons/events/reckoningEvent.png"));
                eventDescription.setText(resources.getString("event.reckoning.description"));
                eventName.setText(resources.getString("event.reckoning.name"));
                break;
            case "roger_feast":
                eventImage.setImage(imageCache.get("icons/events/rogerFeastEvent.png"));
                eventDescription.setText(resources.getString("event.rogerFeast.description"));
                eventName.setText(resources.getString("event.rogerFeast.name"));
                break;
            case "rum_bottle":
                eventImage.setImage(imageCache.get("icons/events/rumBottleEvent.png"));
                eventDescription.setText(resources.getString("event.rumBottle.description"));
                eventName.setText(resources.getString("event.rumBottle.name"));
                break;
            case "submerge":
                eventImage.setImage(imageCache.get("icons/events/submergeEvent.png"));
                eventDescription.setText(resources.getString("event.submerge.description"));
                eventName.setText(resources.getString("event.submerge.name"));
                break;
        }
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
