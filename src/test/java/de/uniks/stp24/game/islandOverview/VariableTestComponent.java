package de.uniks.stp24.game.islandOverview;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class VariableTestComponent extends IslandOverviewTestInitializer{

    final Map<String, Integer> variablesPresets = new HashMap<>();
    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    public final ArrayList<ExplainedVariableDTO> explainedVariableDTOS = new ArrayList<>();

    final Map<String, Integer> siteSlots = Map.of("test1", 3, "test2", 3, "test3", 4, "test4", 4);
    final Map<String, Integer> sites = Map.of("test1", 2, "test2", 3, "test3", 4, "test4", 4);
    final IslandType myTestIsland = IslandType.valueOf("uninhabitable_0");
    final ArrayList<String> buildings = new ArrayList();
    final List<Island> islands = new ArrayList<>();
    final Map<String, Integer> cost = Map.of("energy", 3, "fuel", 2);
    final Map<String,List<SeasonComponent>> _private = new HashMap<>();


    Island testIsland;
    public void initComponents(){
        initializeComponents();

        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("123456").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a", "a", "testEmpireID", "123456", "testUserID", "testEmpire",
                "a", "a", 1, 2, "a", new String[]{"1"}, cost,
                null))).when(this.empireService).getEmpire(any(), any());

        doReturn(Observable.just(new Game("a", "a", "123456", "gameName", "gameOwner", 2, 1, true, 1, 1, null))).when(gamesApiService).getGame(any());
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.123456.empires.testEmpireID.updated"), eq(EmpireDto.class));
        doReturn(Observable.just(new Event<>("games.123456.ticked", new Game("a", "a", "123456", "gameName", "gameOwner", 2, 1, true, 1, 1, null))))
                .when(this.eventListener).listen("games.123456.ticked", Game.class);
        testIsland = new Island(
                "testEmpireID",
                1,
                50,
                50,
                myTestIsland,
                20,
                25,
                2,
                siteSlots,
                sites,
                buildings,
                "1",
                "explored",
                "TestIsland1"
        );

        this.islandAttributeStorage.setIsland(testIsland);
        doReturn(null).when(this.imageCache).get(any());

        variablesPresets.put("buildings.church.build_time", 2);
        variablesPresets.put("buildings.church.cost.energy", 2);
        variablesPresets.put("buildings.church.upkeep.minerals", 2);

        ArrayList<String> traits = new ArrayList<>();
        Empire empire = new Empire(
                "testEmpire",
                "justATest",
                "RED",
                0,
                2,
                traits,
                "uncharted_island0"

        );

        MemberDto member = new MemberDto(
                true,
                "testUser",
                empire,
                "123"
        );

        Map<String, ArrayList<String>> variablesEffect = new HashMap<>();

        ArrayList<Jobs.Job> jobList = new ArrayList<>();
        EffectSourceParentDto effectSourceParentDto = new EffectSourceParentDto(new EffectSourceDto[3]);

        ExplainedVariableDTO explainedVariableDTO1 = new ExplainedVariableDTO("buildings.church.build_time", 1, new ArrayList<>(), 1);
        ExplainedVariableDTO explainedVariableDTO2 = new ExplainedVariableDTO("buildings.church.cost.energy",1, new ArrayList<>(), 1);
        ExplainedVariableDTO explainedVariableDTO3 = new ExplainedVariableDTO("buildings.church.upkeep.minerals", 1, new ArrayList<>(), 1);

        explainedVariableDTOS.add(explainedVariableDTO1);
        explainedVariableDTOS.add(explainedVariableDTO2);
        explainedVariableDTOS.add(explainedVariableDTO3);

        doReturn(Observable.just(variablesPresets)).when(inGameService).getVariablesPresets();
        doReturn(Observable.just(jobList)).when(jobsApiService).getEmpireJobs(any(), any());
        doReturn(Observable.just(effectSourceParentDto)).when(empireApiService).getEmpireEffect(any(), any());
        doReturn(Observable.just(explainedVariableDTOS)).when(gameLogicApiService).getVariablesExplanations(any(), any());

        this.marketComponent.marketService = this.marketService;
        this.marketService.presetsApiService = this.presetsApiService;
        this.marketComponent.presetsApiService = this.presetsApiService;
        this.marketComponent.subscriber = this.subscriber;
        this.inGameController.marketOverviewComponent = this.marketComponent;
        this.marketService.subscriber = this.subscriber;

        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyService.tokenStorage = this.tokenStorage;
        doReturn(Observable.just(new SystemDto[]{})).when(this.gameSystemsApiService).getSystems(any());

        doReturn(Observable.just(_private)).when(this.marketService).getSeasonalTrades(any(),any());

        this.islandsService.isles = islands;
        this.mockFleets();
        this.app.show(this.inGameController);
       clearStyleSheets();
    }

    private void mockFleets(){
        // Mock get Fleets and ships
        ArrayList<Fleets.ReadFleetDTO> fleets = new ArrayList<>(Collections.singleton(new Fleets.ReadFleetDTO("a", "a", "fleetID", "123456", "testEmpireID", "fleetName", "fleetLocation", 4, new HashMap<>(), new HashMap<>())));
        doReturn(Observable.just(fleets)).when(this.fleetApiService).getGameFleets("123456",true);
        doNothing().when(this.fleetService).initializeFleetListeners();
        doNothing().when(this.fleetService).initializeShipListener();
    }
}
