package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestStorageOverview extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
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
    OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;
    @InjectMocks
    IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    DetailsComponent detailsComponent;
    @InjectMocks
    SitesComponent sitesComponent;
    @InjectMocks
    BuildingsComponent buildingsComponent;
    @InjectMocks
    InGameController inGameController;

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> resources1 = Map.of("energy",3, "population", 2);
    Map<String, Integer> resources2 = Map.of("energy",3, "population", 4);
    Map<String, Integer> resources3 = Map.of("energy",5, "population", 6);
    ArrayList<BuildingPresets> buildingPresets = new ArrayList<>();
    ArrayList<DistrictPresets> districtPresets = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameService.setGameStatus(gameStatus);
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;


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

        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));

        SystemUpgrades systemUpgrades = new SystemUpgrades(
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0),
                new UpgradeStatus("1", 0, null, null, 0));
        doReturn(Observable.just(systemUpgrades)).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(buildingPresets)).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(districtPresets)).when(inGameService).loadDistrictPresets();

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
        /*
        waitForFxEvents();
        clickOn("#showStorageButton");
        waitForFxEvents();
        sleep(10);

        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(3,storageOverviewComponent.resourceListView.getItems().getFirst().count());

        empireDtoSubject.onNext(new Event<>("games.testGameID.empires.testEmpireID.updated",
                new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources2 ,
                        null)));
        waitForFxEvents();
        sleep(5);

        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(3,storageOverviewComponent.resourceListView.getItems().getFirst().count());

        empireDtoSubject.onNext(new Event<>("games.testGameID.empires.testEmpireID.updated",
                new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources3 ,
                        null)));
        waitForFxEvents();
        sleep(5);

        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(5,storageOverviewComponent.resourceListView.getItems().getFirst().count());*/
    }

}
