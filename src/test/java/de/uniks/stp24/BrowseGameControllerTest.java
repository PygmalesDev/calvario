package de.uniks.stp24;

import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.controllers.EditAccController;
import de.uniks.stp24.controllers.EditGameController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.BrowseGameService;
import de.uniks.stp24.service.CreateGameService;
import de.uniks.stp24.service.EditGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.bytebuddy.description.ByteCodeElement;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;
import org.w3c.dom.Text;


import javax.inject.Inject;
import javax.inject.Provider;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Flow;

import static org.junit.jupiter.api.Assertions.*;

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
    @InjectMocks
    EditGameController editGameController;
    final Subject<Event<Game>> subject = BehaviorSubject.create();

    @Override
    public void start(Stage stage)  throws Exception{
        Mockito.doReturn(Observable.just(List.of(
                new Game(null, null, "1", "Was geht", "testID2", false, 0,0, null),
                new Game(null, null, "2", "rapapa", "testID", false, 0,0, null)
        ))).when(gamesApiService).findAll();

        Mockito.doReturn(subject).when(eventListener).listen("games.*.*", Game.class);

        super.start(stage);
        app.show(browseGameController);
    }

    @Test
    void testLobbyList(){
        //Create new Game and check if game is listed on ListView
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, browseGameController.gameList.getItems().size());
        subject.onNext(new Event<>("games.3.created", new Game(null, null, "3", "taschaka", "testID2", false, 0,0, null)));

        //Delete existing game and check if game is still listed or not.
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(3, browseGameController.gameList.getItems().size());
        subject.onNext(new Event<>("games.652.deleted", new Game(null, null, "2", "rapapa", "testID", false, 0,0, null)));

        //Check amount of Listview items
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(2, browseGameController.gameList.getItems().size());
    }

    //Check if logout-screen is displayed after clicking on logout button
    @Test
    void logOut(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.log_out_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Login", stage.getTitle());
    }


    @Test
    void editGame(){
        //Set selected Game as one of the games u have created
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().getFirst());
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        //Click on edit game button and check if edit game screen is now displayed.
        clickOn(browseGameController.edit_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("EditGame", stage.getTitle());

        //Click on confirm. No inputs for change was given. Screen do not change
        WaitForAsyncUtils.waitForFxEvents();
        Button confirmButton = lookup("#editGameConfirmButton").queryButton();
        clickOn(confirmButton);
        assertEquals("EditGame", stage.getTitle());
    }

    @Test
    void editNotPossible(){
        //Game which is not yours is selected for edit.
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().get(1));
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(1);
        browseGameController.gameList.getFocusModel().focus(1);

        //Click on confirm changes nothing. Screen is still browse game.
        clickOn(browseGameController.edit_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(stage.getTitle(), "Browse Game");
    }

}
