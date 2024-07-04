package de.uniks.stp24.component.game;

import de.uniks.stp24.component.Captain;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TimerService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Component(view = "HintBubble.fxml")
public class HintBubbleComponent extends Captain {

    @FXML
    Button closeButton;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    GamesApiService gamesApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    TimerService timerService;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    ArrayList<String> possibleHints = Constants.hints;

    int prevSeasonNumber;
    Random random = new Random();

    @Inject
    public HintBubbleComponent(){

    }

    @OnInit
    public void init(){
        PropertyChangeListener callHandleHintTiming = this::handleHintTiming;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SEASON, callHandleHintTiming);
        subscriber.subscribe(gamesApiService.getGame(tokenStorage.getGameId()),
                game -> prevSeasonNumber = game.period(),
                error -> System.out.println("Error on getting game: " + error)
        );
    }

    private void handleHintTiming(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int season = (int) propertyChangeEvent.getNewValue();
            if (season - prevSeasonNumber == 1) {
                removeHint();
            }
            if (season - prevSeasonNumber >= 2){
                setVisible(true);
//                inGameController.showHint();
                prevSeasonNumber += 2;
            }
        }
    }

    public void addRandomTip() {
        String hint = possibleHints.get(random.nextInt(possibleHints.size()));
        setCaptainText(gameResourceBundle.getString(hint));
    }

    public void close(){
        removeHint();
    }

    public void removeHint(){
//        setVisible(false);
//        inGameController.removeCaptainHint();
    }
}
