package de.uniks.stp24.component.game;

import de.uniks.stp24.component.Captain;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

@Component(view = "HintBubble.fxml")
public class HintBubbleComponent extends Captain {

    @FXML
    Button forwardButton;
    @FXML
    Button nextButton;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;
    @Inject
    EmpireService empireService;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    Random random = new Random();

    int lastPeriod = 0;

    int hintCountdown;
    ArrayList<String> possibleHints = Constants.hints;

    ObservableMap<String, Integer> announcements = FXCollections.observableHashMap();

    @Inject
    public HintBubbleComponent(){

    }

    @OnInit
    public void init() {
        setHintCountDown();

        // todo delete
        announcements.put("resource.doubloons", 10);
        announcements.put("resource.provisions", 30);
        announcements.put("resource.coal", 5);
    }

    private void setHintCountDown() {
        // todo change
        // countdown = random.nextInt(20, 40);
        hintCountdown = 2;
    }

    @OnRender
    public void render() {
        rotateCaptain();

        announcements.addListener(new MapChangeListener<String, Integer>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Integer> change) {
                if (announcements.isEmpty()) {
                    nextButton.getStyleClass().removeAll("rightTriangleButton");
                    nextButton.getStyleClass().add("closeButtonHint");
                }
                else  {
                    nextButton.getStyleClass().removeAll("closeButtonHint");
                    nextButton.getStyleClass().add("rightTriangleButton");
                }
            }
        });

        subscriber.subscribe(this.eventListener.listen(String.format("games.%s.ticked", tokenStorage.getGameId()), Game.class),
                result -> {
                    if (lastPeriod != result.data().period()) {
                        lastPeriod = result.data().period();

                        subscriber.subscribe(empireService.getResourceAggregates(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                                aggregateResultDto -> {
                                    // todo change
                                    // announcements.clear();
                                    for (AggregateItemDto item : aggregateResultDto.items()) {
                                        if (item.count() > 0 && item.count() + item.subtotal() <= 0) {
                                            announcements.put(item.variable(), item.count());
                                        }
                                    }
                                },
                                error -> System.out.println("ErrorAggregateSubscriber: " + error));

                        decideWhatToSay();
                    }
                },
                error -> System.out.println("Error on Season: " + error.getMessage())
        );
    }

    public void forward() {

    }

    public void talk(String text) {
        setVisible(true);
        setCaptainText(text);
    }

    private void talkAboutWorry() {
        if (!announcements.isEmpty()) {
            String nextWorry = announcements.keySet().iterator().next();
            talk(worryToText(gameResourceBundle.getString(nextWorry), announcements.remove(nextWorry)));
        }
    }

    private String worryToText(String worry, int count) {
        // todo change text
        return "Bruh, you are broke. You have only " + count + " " + worry + "! Get your act togehther!";
    }

    public void decideWhatToSay(){
        if (announcements.isEmpty()) { // todo check for jobs
            hintCountdown -= 1;
            if (hintCountdown <= 0) {
                talk(gameResourceBundle.getString(possibleHints.get(random.nextInt(possibleHints.size()))));
                setHintCountDown();
            } else {
                silence();
            }
        } else {
            // todo for later
            // talkAboutJobs();
            talkAboutWorry();
        }
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void removeAltS(){
        silence();
        possibleHints.remove("hint.alt.s");
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void removeAltH(){
        silence();
        possibleHints.remove("hint.alt.h");
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void removeAltE(){
        silence();
        possibleHints.remove("hint.alt.e");
    }

    public void silence() {
        announcements.clear();
        setVisible(false);
    }
}
