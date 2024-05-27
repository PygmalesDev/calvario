package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.BubbleComponent;
import de.uniks.stp24.component.GameComponent;
import de.uniks.stp24.component.LogoutComponent;
import de.uniks.stp24.component.WarningComponent;
import de.uniks.stp24.controllers.BrowseGameController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.LogoutResult;
import de.uniks.stp24.model.User;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.*;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.scene.control.Button;
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

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class BrowseGameControllerTest extends ControllerTest {
    @Mock
    EventListener eventListener;
    @Mock
    GamesApiService gamesApiService;

    @Spy
    PopupBuilder popupBuilder;
    @Spy
    BrowseGameService browseGameService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    CreateGameService createGameService;
    @Spy
    EditGameService editGameService;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    GameComponent gameComponent;
    @Spy
    ErrorService errorService;
    @InjectMocks
    LogoutComponent logoutComponent;
    @InjectMocks
    BubbleComponent bubbleComponent;
    @InjectMocks
    WarningComponent warningComponent;


    Game game = new Game(null, null, "1", "Was geht", "testID2", false, 0,0, null);


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
        browseGameController.gameComponent = gameComponent;
        browseGameController.logoutComponent = logoutComponent;
        browseGameController.bubbleComponent = bubbleComponent;
        browseGameController.warningComponent = warningComponent;
        Mockito.doReturn(Observable.just(List.of(
                game,
                new Game(null, null, "2", "rapapa", "testID", false, 0,0, null)
        ))).when(gamesApiService).findAll();

        Mockito.doReturn(subject).when(eventListener).listen("games.*.*", Game.class);

        super.start(stage);
        app.show(browseGameController);
    }

    /*
    ============================================= Test browse game buttons =============================================
     */

    @Test
    void logOut(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.log_out_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(lookup("#logoutButton").queryButton());
    }

    @Test
    void newGame(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.new_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Create Game", stage.getTitle());
    }

    @Test
    void loadGame(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.load_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        //TODO: Wait for PR LoadGame
        //assertEquals("Load Game", stage.getTitle());
    }

    @Test
    void deleteGame(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.del_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        //TODO: Wait for PR Delete Game
        //assertEquals("Delete Game", stage.getTitle());
    }

    @Test
    void editAcc(){
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Browse Game", stage.getTitle());
        clickOn(browseGameController.edit_acc_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Edit Account", stage.getTitle());
    }

    /*
    ============================================= Test ListView of Lobby =============================================
     */

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

    /*
    ============================================= Test edit game =========================================================
     */

    @Test
    void editGameNoInputs(){
        //Set selected Game as one of the games u have created
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().getFirst());
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        //Click on edit game button and check if edit game screen is now displayed.
        clickOn(browseGameController.edit_game_b);
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("Edit Game", stage.getTitle());

        //Click on confirm. No inputs for change was given. Screen do not change
        WaitForAsyncUtils.waitForFxEvents();
        Button confirmButton = lookup("#editGameConfirmButton").queryButton();
        clickOn(confirmButton);
        assertEquals("Edit Game", stage.getTitle());
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

    @Test
    void deleteGameCancel(){
        //doNothing().when(warningComponent).onCancel();
        //doNothing().when(warningComponent).setGameName();
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().get(0));
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#del_game_b");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#cancelButton");
        assertFalse(warningComponent.getParent().isVisible());
        //verify(this.warningComponent).onCancel();
    }

    @Test
    void deleteGameConfirm(){
        //doNothing().when(warningComponent).setGameName();
        //doNothing().when(warningComponent).deleteGame();
        doReturn(Observable.just(new Game("1", "a","b","c","d",true,4, 5, null)) ).when(this.browseGameService).deleteGame();
        WaitForAsyncUtils.waitForFxEvents();
        browseGameController.browseGameService.setGame(browseGameController.gameList.getItems().get(0));
        browseGameController.browseGameService.setTokenStorage();

        browseGameController.gameList.getSelectionModel().clearAndSelect(0);
        browseGameController.gameList.getFocusModel().focus(0);

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#del_game_b");

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#confirmButton");
        verify(browseGameService, times(1)).deleteGame();
        //verify(this.warningComponent).deleteGame();
    }

    @Test
    public void clickOnLogout() {
        prefService.setRefreshToken("lastRefreshToken");
        System.out.println(prefService.getRefreshToken());
        doReturn(Observable.just(new LogoutResult("a")))
                .when(browseGameService).logout(any());
        doReturn(null).when(app).show("/login");

        // Start:
        // Alice sees the Logout
        // Alice is playing Calvario and wants to log out.
        // The game's prefService contains a refresh token for her account
        // The browse games screen is being shown
        assertEquals("Browse Game", stage.getTitle());
        assertNotNull(prefService.getRefreshToken());

        // Alice clicks on logout
        clickOn("#log_out_b");

        waitForFxEvents();

        clickOn("#logoutButton");
        // it's necessary to generate the observable, that the test mocks,
        // because with it a .doOnNext(...) should be invoke
        Observable<LogoutResult> observable = browseGameService.logout("");
        observable.doOnComplete(() -> prefService.removeRefreshToken()).subscribe();
        waitForFxEvents();

        // Alice sees now the login screen
        // The game's prefService does not contain a refresh token for her account
        verify(browseGameService, times(2)).logout("");
        System.out.println(prefService.getRefreshToken());
        verify(app, times(1)).show("/login");
        assertNull(prefService.getRefreshToken());
    }


    @Test
    public void clickOnCancel() {
        // Start:
        // Alice has unintended clicked the logout button
        // and see the logout screen
        assertEquals("Browse Game", stage.getTitle());

        // Alice clicks on cancel
        clickOn("#log_out_b");
        waitForFxEvents();

        clickOn("#cancelButton");

        waitForFxEvents();

        // Alice return to the browse game login screen
        // must be fixed when browsegames.fxml is available
        assertEquals("Browse Game", stage.getTitle());
    }
}
