package de.uniks.stp24;

import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;


import javax.inject.Provider;
import java.util.List;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BrowseGameControllerTest extends ControllerTest {
    @Mock
    EventListener eventListener;
    @Mock
    GamesApiService gamesApiService;
    @Spy
    Subscriber subscriber = new Subscriber();
    @Spy
    Provider<GameComponent> GameComponentProvider = new Provider(){
        @Override
        public GameComponent get() {
            final GameComponent gameComponent = new GameComponent();
            return new GameComponent();
        }
    };

    @InjectMocks
    BrowseGameController browseGameController;

    final Subject<Event<Game>> subject = BehaviorSubject.create();

    @Override
    public void start(Stage stage)  throws Exception{
        Mockito.doReturn(Observable.just(List.of(
                new Game(null, null, "1", "Was geht", null, false, 0,0, null),
                new Game(null, null, "2", "rapapa", null, false, 0,0, null)
        ))).when(gamesApiService).findAll();

        Mockito.doReturn(subject).when(eventListener).listen("games.*.*", Game.class);

        super.start(stage);
        app.show(browseGameController);
    }

    @Test
    void testLobbyList(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, browseGameController.gameList.getItems().size());
        subject.onNext(new Event<>("games.3.created", new Game(null, null, "3", "taschaka", null, false, 0,0, null)));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(3, browseGameController.gameList.getItems().size());
        subject.onNext(new Event<>("games.652.deleted", new Game(null, null, "2", "rapapa", null, false, 0,0, null)));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, browseGameController.gameList.getItems().size());

    }

    @Test
    void logOut(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.log_out_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Login", stage.getTitle());
    }

}
