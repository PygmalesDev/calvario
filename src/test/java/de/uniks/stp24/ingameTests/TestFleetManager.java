package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.fleetManager.ChangeFleetComponent;
import de.uniks.stp24.component.game.fleetManager.FleetComponent;
import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import de.uniks.stp24.component.game.fleetManager.NewFleetComponent;
import de.uniks.stp24.rest.FleetApiService;
import de.uniks.stp24.rest.ShipsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static de.uniks.stp24.model.Fleets.*;
import static de.uniks.stp24.model.Ships.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestFleetManager extends ControllerTest {
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    ImageCache imageCache;
    @InjectMocks
    FleetService fleetService;
    @InjectMocks
    ShipService shipService;
    @Spy
    VariableService variableService;
    @InjectMocks
    IslandsService islandsService;
    @Spy
    JobsService jobsService;
    @Spy
    FleetApiService fleetApiService;
    @Spy
    ShipsApiService shipsApiService;

    @InjectMocks
    FleetManagerComponent fleetManagerComponent;
    @InjectMocks
    NewFleetComponent newFleetComponent;
    @InjectMocks
    ChangeFleetComponent changeFleetComponent;

    protected final String
            GAME_ID = "123456",
            EMPIRE_ID = "testEmpireID",
            FLEET_ID = "testFleetID",
            LOCATION = "homeIsland";

    final Subject<Event<Ship>> SHIP_SUBJECT = BehaviorSubject.create();
    final Subject<Event<Ship>> SHIP_SUBJECT2 = BehaviorSubject.create();
    final Subject<Event<Fleet>> FLEET_SUBJECT = BehaviorSubject.create();

    protected final List<ReadFleetDTO> FLEETS = new ArrayList<>(Arrays.asList(
            new ReadFleetDTO("a", "a", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 2, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID2", GAME_ID, EMPIRE_ID, "fleetName2", LOCATION, 4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID3", GAME_ID, EMPIRE_ID, "fleetName3", LOCATION, 1, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID4", GAME_ID, EMPIRE_ID, "fleetName4", "somewhereElse", 4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID5", GAME_ID, "enemyEmpire", "fleetName5", "somewhereElse", 4, new HashMap<>(), new HashMap<>())
    ));

    protected final List<ShipType> BLUEPRINTS = new ArrayList<>(Arrays.asList(
            new ShipType("explorer",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("colonizer",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("interceptor",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>())
           ));

    final Provider<FleetComponent>  fleetComponentProvider = () -> new FleetComponent(this.fleetManagerComponent, this.tokenStorage, this.subscriber, this.fleetService);


    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.newFleetComponent.islandsService = this.islandsService;
        this.fleetManagerComponent.fleetService = this.fleetService;
        this.fleetManagerComponent.shipService = this.shipService;
        this.fleetManagerComponent.fleetComponentProvider = this.fleetComponentProvider;
        this.fleetManagerComponent.changeFleetComponent = this.changeFleetComponent;
        this.fleetManagerComponent.newFleetComponent = this.newFleetComponent;

        // Mock TokenStorage
        doReturn("123456").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();

        // Mock fleet and ship listener
        when(this.eventListener.listen("games." + GAME_ID + ".fleets.*.*", Fleet.class)).thenReturn(FLEET_SUBJECT);
        when(this.eventListener.listen("games." + GAME_ID + ".fleets.*.ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);
        //when(this.eventListener.listen("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);

        // Mock getting all fleets in a game
        when(this.fleetApiService.getGameFleets(GAME_ID, true)).thenReturn(Observable.just((ArrayList<ReadFleetDTO>) FLEETS));

        // Mock ship types
        this.shipService.shipTypesAttributes = (ArrayList<ShipType>) BLUEPRINTS;

        this.app.show(this.fleetManagerComponent);
    }

    @BeforeEach
    public void initializeListener(){
        this.fleetService.loadGameFleets();
        this.fleetService.initializeFleetListeners();
        this.fleetService.initializeShipListener();
        this.fleetManagerComponent.showFleets();
    }

    @Test
    public void deleteFleet(){
        final Fleet fleet2 = new Fleet("a", "a", "fleetID2", GAME_ID, EMPIRE_ID, "fleetName3",
                LOCATION, 1, new HashMap<>(), new HashMap<>(), new HashMap<>(), null);
        when(this.fleetApiService.deleteFleet(any(),any())).thenReturn(Observable.just(fleet2));

        waitForFxEvents();
        assertEquals(4, fleetManagerComponent.fleets.size());
        clickOn("#deleteFleetButton_fleetID2");
        waitForFxEvents();
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.deleted", fleet2));
        waitForFxEvents();
        assertEquals(3, fleetManagerComponent.fleets.size());
    }

}
