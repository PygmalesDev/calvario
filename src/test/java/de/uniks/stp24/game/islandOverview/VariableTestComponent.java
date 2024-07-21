package de.uniks.stp24.game.islandOverview;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a", "a", "testEmpireID", "testGameID", "testUserID", "testEmpire",
                "a", "a", 1, 2, "a", new String[]{"1"}, cost,
                null))).when(this.empireService).getEmpire(any(), any());

        doReturn(Observable.just(new Game("a", "a", "testGameID", "gameName", "gameOwner", 2, 1, true, 1, 1, null))).when(gamesApiService).getGame(any());
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));
        doReturn(Observable.just(new Event<>("games.testGameID.ticked", new Game("a", "a", "testGameID", "gameName", "gameOwner", 2, 1, true, 1, 1, null))))
                .when(this.eventListener).listen("games.testGameID.ticked", Game.class);
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

        ExplainedVariableDTO explainedVariableDTO1 = new ExplainedVariableDTO("buildings.church.build_time", 1, new ArrayList<Sources>(), 1);
        ExplainedVariableDTO explainedVariableDTO2 = new ExplainedVariableDTO("buildings.church.cost.energy",1, new ArrayList<Sources>(), 1);
        ExplainedVariableDTO explainedVariableDTO3 = new ExplainedVariableDTO("buildings.church.upkeep.minerals", 1, new ArrayList<Sources>(), 1);

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

        when(this.presetsApiService.getVariables()).thenReturn(Observable.just(new HashMap<>()));
        doReturn(Observable.just(_private)).when(this.marketService).getSeasonalTrades(any(),any());

        this.islandsService.isles = islands;
        this.app.show(this.inGameController);
       clearStyleSheets();
    }
}
