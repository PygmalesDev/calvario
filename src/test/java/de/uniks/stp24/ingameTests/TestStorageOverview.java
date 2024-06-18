package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestStorageOverview extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    EmpireApiService empireApiService;
    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;
    @Spy
    TimerService timerService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    LanguageService languageService;
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    EmpireService empireService;
    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);

    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    SettingsComponent settingsComponent;
    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    ClockComponent clockComponent;

    @InjectMocks
    InGameController inGameController;

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();
    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();

    Map<String, Integer> resources1 = new LinkedHashMap<>() {{
        put("energy", 3);
        put("population", 2);
    }};
    Map<String, Integer> resources2 = new LinkedHashMap<>() {{
        put("energy", 4);
        put("population", 4);
    }};
    Map<String, Integer> resources3 = new LinkedHashMap<>() {{
        put("energy", 5);
        put("population", 4);
    }};

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.empireService.empireApiService = this.empireApiService;
        this.inGameService.setGameStatus(gameStatus);

        // Mock TokenStorage
        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();

        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, resources1 ,
                null))).when(this.empireService).getEmpire(any(),any());

        doReturn(Observable.just(new Game("a","a","testGameID", "gameName", "gameOwner", true,1,1,null ))).when(gamesApiService).getGame(any());

        // Mock empire listener
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));

        // Mock season listener
        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.ticked"), eq(Game.class));

        this.app.show(this.inGameController);
    }

    @Test
    public void openStorage(){
        waitForFxEvents();
        // Storage is closed
        assertFalse(this.inGameController.storageOverviewContainer.isVisible());
        // Open storage
        clickOn("#showStorageButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewContainer.isVisible());
        // Close storage with Button in Ingame
        clickOn("#showStorageButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewContainer.isVisible());
        // Open again
        clickOn("#showStorageButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewContainer.isVisible());
        // Close storage with button in StorageOverviewComponent
        clickOn("#closeStorageOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewContainer.isVisible());
    }

    @Test
    public void updateResourcesWithEmpireUpdate(){
        waitForFxEvents();
        clickOn("#showStorageButton");
        waitForFxEvents();

        // resourceList: 3 energy, 2 population
        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(3,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(2,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(2,resourcesService.getResourceCount("population"));

        empireDtoSubject.onNext(new Event<>("games.testGameID.empires.testEmpireID.updated",
                new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources2 ,
                        null)));
        waitForFxEvents();

        // resourceList: 4 energy, 4 population
        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(4,resourcesService.getResourceCount("population"));

        empireDtoSubject.onNext(new Event<>("games.testGameID.empires.testEmpireID.updated",
                new EmpireDto("a","b","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources3 ,
                        null)));
        waitForFxEvents();

        // resourceList: 5 energy, 4 population
        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(5,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(4,resourcesService.getResourceCount("population"));
    }


    @Test
    public void updateResourcesWithSeasonChange() {
        waitForFxEvents();
        AggregateItemDto energyAggregate = new AggregateItemDto("energy",4,1 );
        AggregateItemDto populationAggregate = new AggregateItemDto("population",4,2);
        AggregateResultDto aggregateResultDto = new AggregateResultDto(8, new AggregateItemDto[]{energyAggregate, populationAggregate});

        // Mock getEmpire (second time)
        when(this.empireService.getEmpire(any(),any()))
                .thenReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources2 ,
                        null)));

        // Mock get aggregates
        when(this.empireService.getResourceAggregates(any(),any())).thenReturn(Observable.just(aggregateResultDto));

        assertEquals(3,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(0,storageOverviewComponent.resourceListView.getItems().getFirst().changePerSeason());
        assertEquals(2,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(0,storageOverviewComponent.resourceListView.getItems().getLast().changePerSeason());


        // Season change: energy +1, population +2
        gameSubject.onNext(new Event<>("games.testGameID.ticked",
                new Game("a","b","testGameID","testGame", "testUserID",
                        true, 2, 1, null)));
        waitForFxEvents();

        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(1,storageOverviewComponent.resourceListView.getItems().getFirst().changePerSeason());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(2,storageOverviewComponent.resourceListView.getItems().getLast().changePerSeason());
    }

}
