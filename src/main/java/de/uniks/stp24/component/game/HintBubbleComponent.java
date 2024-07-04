package de.uniks.stp24.component.game;

import de.uniks.stp24.component.Captain;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
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
    Button closeButton;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;
    @Inject
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    ArrayList<String> possibleHints = Constants.hints;

    int countdown;
    Random random = new Random();

    @Inject
    public HintBubbleComponent(){

    }

    @OnInit
    public void init() {
        setCountDown();
    }

    private void setCountDown() {
        countdown = random.nextInt(2, 5);
    }

    @OnRender
    public void render() {
        subscriber.subscribe(this.eventListener.listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                result -> {
                    countdown -= 1;
                    if (countdown <= 0) {
                        showRandomTip();
                        setCountDown();
                    }
                },
                error -> System.out.println("Error on Season: " + error.getMessage())
        );
    }

    public void showRandomTip() {
        setVisible(true);
        String hint = possibleHints.get(random.nextInt(possibleHints.size()));
        setCaptainText(gameResourceBundle.getString(hint));
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void removeAltS(){
        close();
        possibleHints.remove("hint.alt.s");
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void removeAltH(){
        close();
        possibleHints.remove("hint.alt.h");
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void removeAltE(){
        close();
        possibleHints.remove("hint.alt.e");
    }

    public void close(){
        setVisible(false);
    }
}
