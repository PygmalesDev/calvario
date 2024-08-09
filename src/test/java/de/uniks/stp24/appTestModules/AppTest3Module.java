package de.uniks.stp24.appTestModules;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;

import java.util.*;

import static org.mockito.Mockito.*;

public class AppTest3Module extends LobbyTestLoader {

    protected final String
            GAME_ID = "123456",
            EMPIRE_ID = "testEmpireID",
            USER_ID = "testUserID";

    protected final Map<String, Integer> DEV_RESOURCES = Map.of(
            "credits", 1000000000,
            "energy",1000000000,
            "minerals",1000000000,
            "food", 1000000000,
            "fuel", 1000000000,
            "research", 1000000000,
            "alloys", 1000000000,
            "consumer_goods", 1000000000
    );

    protected final BuildingDto BUILDING_DTO = new BuildingDto("refinery", 3,
            Map.of("minerals", 100),
            Map.of("minerals", 10, "energy", 15),
            Map.of("fuel", 10));

    protected final DistrictAttributes DISTRICT_ATTRIBUTES = new DistrictAttributes("energy", 3,
            new HashMap<>(),
            Map.of("minerals", 75),
            Map.of("minerals", 2),
            Map.of("energy", 30));

    protected final String[] JOB_EVENT_PATHS = new String[]{
            "games.123456.empires.testEmpireID.jobs.jobClaimingID_1.",
            "games.123456.empires.testEmpireID.jobs.jobClaimingID_2.",
            "games.123456.empires.testEmpireID.jobs.jobBuildingID.",
            "games.123456.empires.testEmpireID.jobs.jobSiteID."
    };

    protected final String JOB_EVENT_PATH = "games.123456.empires.testEmpireID.jobs.*.";

    protected final GameStatus GAME_STATUS = new GameStatus();

    protected final Game GAME = new Game("0", "0", GAME_ID, "TestGameName", USER_ID,
            1, 1, true, 0, 0, null);

    protected final UpdateGameResultDto RESULT_DTO = new UpdateGameResultDto(GAME.createdAt(), GAME.updatedAt(),
            GAME_ID, GAME.name(), GAME.owner(), GAME.started(), GAME.speed(), GAME.period(), GAME.settings());

    protected final Empire EMPIRE = new Empire("TestEmpire", "This is a test empire",
            "#FF00FF", 3, 4, new ArrayList<>(), "regular");
    protected final Empire EMPIRE2 = new Empire("TestEmpire", "This is a test empire",
            "#FF00FF", 3, 4, new ArrayList<>(List.of("__dev__")), "regular");

    protected final EmpireDto EMPIRE_DTO = new EmpireDto("0", "0", EMPIRE_ID, GAME_ID, USER_ID,
            EMPIRE.name(), EMPIRE.description(), EMPIRE.color(), EMPIRE.flag(), EMPIRE.portrait(),
            EMPIRE.homeSystem(), EMPIRE.traits().toArray(new String[]{}), DEV_RESOURCES, new String[]{});

    protected final ReadEmpireDto READ_EMPIRE_DTO = new ReadEmpireDto(EMPIRE_DTO.createdAt(), EMPIRE_DTO.updatedAt(),
            EMPIRE_ID, GAME_ID, USER_ID, EMPIRE.name(), EMPIRE.description(), EMPIRE.color(), EMPIRE.flag(), EMPIRE.portrait(),
            EMPIRE.homeSystem());

    protected final Trait[] TRAITS = new Trait[]{
            new Trait("__dev__", new EffectDto[]{
                            new EffectDto("resources.credits.starting", 0, 0, 1000000000),
                            new EffectDto("resources.energy.starting", 0, 0, 1000000000),
                            new EffectDto("resources.minerals.starting", 0, 0, 1000000000),
                            new EffectDto("resources.food.starting", 0, 0, 1000000000),
                            new EffectDto("resources.fuel.starting", 0, 0, 1000000000),
                            new EffectDto("resources.research.starting", 0, 0, 1000000000),
                            new EffectDto("resources.alloys.starting", 0, 0, 1000000000),
                            new EffectDto("resources.consumer_goods.starting", 0, 0, 1000000000)
                    }, 0, null),
            new Trait("prepared",
                    new EffectDto[]{new EffectDto("resources.energy.starting", 0, 0, 200)},
                    1, new String[]{"unprepared"}),
            new Trait("unprepared",
                    new EffectDto[]{new EffectDto("resources.energy.starting", 0, 0, -200)},
                    1, new String[]{"prepared"})
                    };

    protected final Map<String, Integer> VARIABLE_PRESETS = Map.of(
            "districts.energy.cost.minerals", 75,
            "districts.energy.upkeep.minerals", 2,
            "districts.energy.production.energy", 30,
            "buildings.refinery.cost.minerals", 100,
            "buildings.refinery.upkeep.minerals", 10,
            "buildings.refinery.upkeep.energy", 15,
            "buildings.refinery.production.fuel", 10,
            "systems.colonized.cost.energy", 100
    );

    protected final Map<String, Double> MARKET_VARIABLES = Map.of(
            "resources.energy.credit_value", 1.0,
            "resources.minerals.credit_value", 1.0,
            "resources.food.credit_value", 1.0,
            "resources.fuel.credit_value", 5.0,
            "resources.alloys.credit_value", 8.0,
            "resources.consumer_goods.credit_value", 6.0,
            "empire.market.fee", 0.3
    );

    protected final ArrayList<ExplainedVariableDTO> VARIABLE_EXPLANATIONS = new ArrayList<>(List.of(
            new ExplainedVariableDTO("districts.energy.cost.minerals", 75, new ArrayList<>(), 75),
            new ExplainedVariableDTO("districts.energy.upkeep.minerals", 2, new ArrayList<>(), 2),
            new ExplainedVariableDTO("districts.energy.production.energy", 30, new ArrayList<>(), 30),
            new ExplainedVariableDTO("buildings.refinery.cost.minerals", 100, new ArrayList<>(), 100),
            new ExplainedVariableDTO("buildings.refinery.upkeep.minerals", 10, new ArrayList<>(), 10),
            new ExplainedVariableDTO("buildings.refinery.upkeep.energy", 15, new ArrayList<>(), 15),
            new ExplainedVariableDTO("buildings.refinery.production.fuel", 10, new ArrayList<>(), 10),
            new ExplainedVariableDTO("systems.colonized.cost.energy", 100, new ArrayList<>(), 100),
            new ExplainedVariableDTO("resources.energy.credit_value", 1.0, new ArrayList<>(), 1.0),
            new ExplainedVariableDTO("resources.minerals.credit_value", 1.0, new ArrayList<>(), 1.0),
            new ExplainedVariableDTO("resources.food.credit_value", 1.0, new ArrayList<>(), 1.0),
            new ExplainedVariableDTO("resources.fuel.credit_value", 5.0, new ArrayList<>(), 5.0),
            new ExplainedVariableDTO("resources.alloys.credit_value", 8.0, new ArrayList<>(), 8.0),
            new ExplainedVariableDTO("resources.consumer_goods.credit_value", 6.0, new ArrayList<>(), 6.0),
            new ExplainedVariableDTO("resources.consumer_goods.empire.market.fee", 0.3, new ArrayList<>(), 0.3)));

    protected final SystemDto[] GAME_SYSTEMS = new SystemDto[]{
            new SystemDto("0", "0", "islandID_1", GAME_ID, "regular", "TestIslandOne",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.colonized, 13, Map.of("islandID_2", 3, "islandID_3", 3), 50, 50, EMPIRE_ID, 100),
            new SystemDto("0", "0", "islandID_2", GAME_ID, "regular", "TestIslandTwo",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.unexplored, 13, Map.of("islandID_1", 3, "islandID_3", 3), 63, 52, null, 100),
            new SystemDto("0", "0", "islandID_3", GAME_ID, "regular", "TestIslandThree",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.explored, 13, Map.of("islandID_1", 3, "islandID_2", 3), 55, 62, null, 100)
    };

    protected final CreateSystemsDto CREATE_SYSTEM_DTO = new CreateSystemsDto(
            "0", "0", "islandID_1", GAME_ID, IslandType.regular, "TestIslandOne",
            Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
            "colonized", 13, Map.of("islandID_2", 3, "islandID_3", 3), 50, 50, EMPIRE_ID);

    protected final Jobs.Job[] JOBS = new Jobs.Job[]{
            new Jobs.Job("0", "0",
                    "jobClaimingID_1", 0, 3, GAME_ID, EMPIRE_ID, "islandID_2", 0,
                    "upgrade", null, null, null, new HashMap<>(), null),
            new Jobs.Job("0", "0",
                    "jobClaimingID_2", 0, 12, GAME_ID, EMPIRE_ID, "islandID_3", 0,
                    "upgrade", null, null, null, new HashMap<>(), null),
            new Jobs.Job("0", "0",
                    "jobBuildingID", 0, 12, GAME_ID, EMPIRE_ID, "islandID_1", 0,
                    "building", "refinery", null, null, new HashMap<>(), null),
            new Jobs.Job("0", "0",
                    "jobSiteID", 0, 12, GAME_ID, EMPIRE_ID, "islandID_1", 0,
                    "district", null, "energy", null, new HashMap<>(), null),
            new Jobs.Job("0", "0",
                    "jobTechnologyID", 0, 12, GAME_ID, EMPIRE_ID, "islandID_1", 0,
                    "technology", null, null, "society", new HashMap<>(), null)
    };

    protected final TechnologyExtended TECHNOLOGY = new TechnologyExtended("society", new Effect[]{
        new Effect("technologies.society.cost_multiplier", 0, 0.9, 0)},
            new String[]{"society"}, 2, null, null);

    protected final ExplainedVariableDTO EXPLAINED_VARIABLE = new ExplainedVariableDTO(
            "technologies.society.cost_multiplier", 0.9, new ArrayList<>(), 0.9);

    protected final ArrayList<TechnologyExtended> TECHNOLOGIES = new ArrayList<>(List.of(TECHNOLOGY));

    protected final ArrayList<ExplainedVariableDTO> EXPLAINED_VARIABLES = new ArrayList<>(List.of(EXPLAINED_VARIABLE));

    protected final UpdateEmpireMarketDto MARKET_UPDATE_DTO = new UpdateEmpireMarketDto(
            DEV_RESOURCES, null, null, null);

    protected final AggregateResultDto RESOURCE_AGGREGATES = new AggregateResultDto(-437, new AggregateItemDto[]{
            new AggregateItemDto("resources.credits.periodic", 1, -112),
            new AggregateItemDto("resources.population.periodic", 1, 0),
            new AggregateItemDto("resources.energy.periodic", 1, -122),
            new AggregateItemDto("resources.minerals.periodic", 1, -99),
            new AggregateItemDto("resources.food.periodic", 1, -156),
            new AggregateItemDto("resources.fuel.periodic", 1, 14),
            new AggregateItemDto("resources.research.periodic", 1, 10),
            new AggregateItemDto("resources.alloys.periodic", 1, -1),
            new AggregateItemDto("resources.consumer_goods.periodic", 1, 29),
    });

    protected final EffectSourceParentDto SOURCE_PARENT_DTO = new EffectSourceParentDto(new EffectSourceDto[]{});

    protected final MemberDto MEMBER_DTO = new MemberDto(true, USER_ID, EMPIRE, "password");
    protected final MemberDto MEMBER_DTO2 = new MemberDto(true, USER_ID, EMPIRE2, "password");

    protected final Subject<Event<MemberDto>> MEMBER_DTO_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Game>> GAME_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<SystemDto>> SYSTEMDTO_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<EmpireDto>> EMPIRE_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Jobs.Job>> JOB_SUBJECT = BehaviorSubject.create();

    protected int gameTicks = 0;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.initializeApiMocks();
        this.initializeEventListenerMocks();
        this.loadUnloadableData();
        this.mockFleets();
    }

    private void initializeEventListenerMocks() {
        doReturn(MEMBER_DTO_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".members.*.*", MemberDto.class);
        doReturn(GAME_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".updated", Game.class);
        doReturn(GAME_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".ticked", Game.class);
        doReturn(EMPIRE_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".empires." + EMPIRE_ID + ".updated", EmpireDto.class);
        doReturn(SYSTEMDTO_SUBJECT).when(this.eventListener)
                .listen(String.format("games.%s.systems.%s.updated", GAME_ID, "*"),  SystemDto.class);

        when(this.eventListener.listen(JOB_EVENT_PATH + "*", Jobs.Job.class)).thenReturn(JOB_SUBJECT);
        when(this.eventListener.listen(JOB_EVENT_PATHS[0] + "*", Jobs.Job.class)).thenReturn(JOB_SUBJECT);
        when(this.eventListener.listen(JOB_EVENT_PATHS[1] + "*", Jobs.Job.class)).thenReturn(JOB_SUBJECT);
        when(this.eventListener.listen(JOB_EVENT_PATHS[2] + "*", Jobs.Job.class)).thenReturn(JOB_SUBJECT);
        when(this.eventListener.listen(JOB_EVENT_PATHS[3] + "*", Jobs.Job.class)).thenReturn(JOB_SUBJECT);
    }

    private void initializeApiMocks() {
        doReturn(null).when(this.imageCache).get(any());
        doNothing().when(this.saveLoadService).saveGang(any());

        when(this.tokenStorage.getEmpireId()).thenReturn(this.EMPIRE_ID);
        when(this.tokenStorage.getGameId()).thenReturn(GAME_ID);
        when(this.tokenStorage.getUserId()).thenReturn(USER_ID);

        when(this.inGameService.getGameStatus()).thenReturn(GAME_STATUS);

        doAnswer(inv -> {this.joinGameHelper.joinGame(GAME_ID);return Observable.just(RESULT_DTO);})
                .when(this.gamesApiService).startGame(any(), any());
        when(this.gamesApiService.getGame(any())).thenReturn(Observable.just(GAME));

        when(this.empireApiService.getSeasonalTrades(any(), any())).thenReturn(Observable.just(new SeasonalTradeDto(new HashMap<>())));

        when(this.presetsApiService.getVariablesPresets()).thenReturn(Observable.just(VARIABLE_PRESETS));
        when(this.presetsApiService.getVariablesEffects()).thenReturn(Observable.just(new HashMap<>()));
        when(this.presetsApiService.getVariables()).thenReturn(Observable.just(MARKET_VARIABLES));
        when(this.presetsApiService.getTechnology(any())).thenReturn(Observable.just(TECHNOLOGY));
        when(this.presetsApiService.getTechnologies()).thenReturn(Observable.just(TECHNOLOGIES));
        when(this.presetsApiService.getTraitsPreset()).thenReturn(Observable.just(TRAITS));

        when(this.gameLogicApiService.getVariablesExplanations(any(), any())).thenReturn(Observable.just(VARIABLE_EXPLANATIONS));

        doAnswer(inv -> {
            this.app.show("/lobby", Map.of("gameid", GAME_ID));
            return Observable.empty();
        }).when(this.gameMembersApiService).patchMember(any(), any(), any());
        when(this.gameSystemsApiService.updateBuildings(any(), any(), any())).thenReturn(Observable.just(CREATE_SYSTEM_DTO));
        when(this.gameMembersApiService.getMembers(any())).thenReturn(Observable.just(new MemberDto[]{MEMBER_DTO2}));
        when(this.gameSystemsApiService.getSystem(any(), any())).thenReturn(Observable.just(GAME_SYSTEMS[0]));
        when(this.gameMembersApiService.getMember(any(), any())).thenReturn(Observable.just(MEMBER_DTO));
        when(this.gameSystemsApiService.getBuilding(any())).thenReturn(Observable.just(BUILDING_DTO));
        when(this.gameSystemsApiService.getSystems(any())).thenReturn(Observable.just(GAME_SYSTEMS));

        when(this.empireApiService.getSeasonalTrades(any(), any())).thenReturn(Observable.just(new SeasonalTradeDto(null)));
        when(this.empireApiService.saveSeasonalComponents(any(), any(), any())).thenReturn(Observable.just(MARKET_UPDATE_DTO));
        when(this.empireApiService.updateEmpireMarket(any(), any(), any())).thenReturn(Observable.just(MARKET_UPDATE_DTO));
        when(this.empireApiService.getResourceAggregates(any(), any())).thenReturn(Observable.just(RESOURCE_AGGREGATES));
        when(this.empireApiService.getEmpiresDtos(anyString())).thenReturn(Observable.just(new EmpireDto[]{EMPIRE_DTO}));
        when(this.empireApiService.getEmpires(any())).thenReturn(Observable.just(new ReadEmpireDto[]{READ_EMPIRE_DTO}));
        when(this.empireApiService.getEmpireEffect(any(), any())).thenReturn(Observable.just(SOURCE_PARENT_DTO));
        when(this.empireApiService.getEmpire(any(), any())).thenReturn(Observable.just(EMPIRE_DTO));

        when(this.jobsApiService.deleteJob(anyString(), anyString(), any())).thenReturn(Observable.just(JOBS[3]));
        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));
        when(this.jobsApiService.createNewJob(anyString(), anyString(), any(Jobs.JobDTO.class)))
                .thenReturn(Observable.just(JOBS[3])).thenReturn(Observable.just(JOBS[2]))
                .thenReturn(Observable.just(JOBS[0])).thenReturn(Observable.just(JOBS[1]))
                .thenReturn(Observable.just(JOBS[4]));

        when(this.variableService.getFirstHalfOfVariables()).thenReturn(Observable.just(EXPLAINED_VARIABLES));
        when(this.variableService.getSecondHalfOfVariables()).thenReturn(Observable.just(EXPLAINED_VARIABLES));

        doAnswer(inv -> this.app.show(this.gangCreationController)).when(this.app).show(eq("/creation"), any());
        doAnswer(inv -> this.app.show(this.inGameController)).when(this.app).show(eq("/ingame"), any());
        doAnswer(inv -> this.app.show(this.lobbyController)).when(this.app).show(eq("/lobby"), any());

        doNothing().when(this.contactsComponent).loadEmpireWars();
    }

    private void loadUnloadableData() {
        this.islandAttributeStorage.systemUpgradeAttributes = new SystemUpgrades(
                null, null, new UpgradeStatus("0","upgraded", 1, 0, Map.of("energy", 100),
                Map.of("energy", 100), 0), null, null);
        this.islandAttributeStorage.buildingsAttributes = new ArrayList<>(List.of(new BuildingAttributes(
                BUILDING_DTO.id(), BUILDING_DTO.build_time(), BUILDING_DTO.cost(), BUILDING_DTO.upkeep(),
                BUILDING_DTO.production())));
        this.islandAttributeStorage.districtAttributes = new ArrayList<>(List.of(DISTRICT_ATTRIBUTES));
    }

    private void mockFleets(){
        // Mock get Fleets and ships
        ArrayList<Fleets.ReadFleetDTO> fleets = new ArrayList<>(Collections.singleton(new Fleets.ReadFleetDTO("a", "a", "fleetID", "123456", "testEmpireID", "fleetName", "fleetLocation", new HashMap<>())));
        doReturn(Observable.just(fleets)).when(this.fleetApiService).getGameFleets("123456");//,true);
        doNothing().when(this.fleetService).initializeFleetListeners();
        doNothing().when(this.fleetService).onFleetCreated(any());
        doNothing().when(this.fleetService).loadGameFleets();
//        doNothing().when(this.fleetService).initializeShipListener();
    }

    protected Event<Game> tickGame() {
        return new Event<>("games." + GAME_ID + ".ticked",
                new Game("0", "0", GAME_ID, "TestGameName", USER_ID,
                1, 1, true, 0, ++this.gameTicks, null));
    }
}
