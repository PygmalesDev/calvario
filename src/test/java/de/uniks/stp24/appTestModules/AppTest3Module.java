package de.uniks.stp24.appTestModules;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AppTest3Module extends LobbyTestLoader {

    protected final String
            GAME_ID = "testGameID",
            EMPIRE_ID = "testEmpireID",
            USER_ID = "testUserID";

    protected final String JOB_EVENT_PATH = String.format("games.%s.empires.%s.jobs.*.", GAME_ID, EMPIRE_ID);

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
            EMPIRE.homeSystem(), EMPIRE.traits().toArray(new String[]{}), Map.of("energy", 100), new String[]{});

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

    protected final SystemDto[] GAME_SYSTEMS = new SystemDto[]{
            new SystemDto("0", "0", "islandID_1", GAME_ID, "regular", "TestIslandOne",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.colonized, 13, Map.of("islandID_2", 3, "islandID_3", 3), 50, 50, EMPIRE_ID),
            new SystemDto("0", "0", "islandID_2", GAME_ID, "regular", "TestIslandTwo",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.unexplored, 13, Map.of("islandID_1", 3, "islandID_3", 3), 63, 52, null),
            new SystemDto("0", "0", "islandID_3", GAME_ID, "regular", "TestIslandThree",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.explored, 13, Map.of("islandID_1", 3, "islandID_2", 3), 55, 62, null)
    };

    protected final Jobs.Job[] JOBS = new Jobs.Job[]{
            new Jobs.Job("0", "0",
                    "jobClaimingID_1", 0, 3, GAME_ID, EMPIRE_ID, "islandID_2", 0,
                    "upgrade", null, null, null, new HashMap<>(), null),
            new Jobs.Job("0", "0",
                    "jobClaimingID_2", 0, 12, GAME_ID, EMPIRE_ID, "islandID_3", 0,
                    "upgrade", null, null, null, new HashMap<>(), null)
    };

    protected final EffectSourceParentDto SOURCE_PARENT_DTO = new EffectSourceParentDto(new EffectSourceDto[]{});

    protected final MemberDto MEMBER_DTO = new MemberDto(true, USER_ID, EMPIRE, "password");
    protected final MemberDto MEMBER_DTO2 = new MemberDto(true, USER_ID, EMPIRE2, "password");

    protected final Subject<Event<MemberDto>> MEMBER_DTO_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Game>> GAME_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<EmpireDto>> EMPIRE_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Jobs.Job>> JOB_SUBJECT = BehaviorSubject.create();

    protected int gameTicks = 0;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.initializeApiMocks();
        this.initializeEventListenerMocks();
        this.loadUnloadableData();
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
        doReturn(JOB_SUBJECT).when(this.eventListener)
                .listen(String.format(JOB_EVENT_PATH + "*", GAME_ID, EMPIRE_ID), Jobs.Job.class);
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

        when(this.presetsApiService.getVariablesPresets()).thenReturn(Observable.just(new HashMap<>()));
        when(this.presetsApiService.getVariablesEffects()).thenReturn(Observable.just(new HashMap<>()));
        when(this.presetsApiService.getTraitsPreset()).thenReturn(Observable.just(TRAITS));

        when(this.gameSystemsApiService.getSystems(any())).thenReturn(Observable.just(GAME_SYSTEMS));

        doAnswer(inv -> this.app.show("/lobby")).when(this.gameMembersApiService).patchMember(any(), any(), any());
        when(this.gameMembersApiService.getMembers(any())).thenReturn(Observable.just(new MemberDto[]{MEMBER_DTO2}));
        when(this.gameMembersApiService.getMember(any(), any())).thenReturn(Observable.just(MEMBER_DTO));



        when(this.empireApiService.getEmpireEffect(any(), any())).thenReturn(Observable.just(SOURCE_PARENT_DTO));
        when(this.empireApiService.getEmpire(any(), any())).thenReturn(Observable.just(EMPIRE_DTO));
        when(this.empireApiService.getEmpires(any())).thenReturn(Observable.just(new ReadEmpireDto[]{READ_EMPIRE_DTO}));

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));
        when(this.jobsApiService.createNewJob(any(), any(), any()))
                .thenReturn(Observable.just(JOBS[0]))
                .thenReturn(Observable.just(JOBS[1]));

        doAnswer(inv -> this.app.show(this.gangCreationController)).when(this.app).show(eq("/creation"), any());
        doAnswer(inv -> this.app.show(this.inGameController)).when(this.app).show(eq("/ingame"), any());
        doAnswer(inv -> this.app.show(this.lobbyController)).when(this.app).show(eq("/lobby"), any());
    }

    private void loadUnloadableData() {
        this.islandAttributeStorage.systemUpgradeAttributes = new SystemUpgrades(
                null, null, new UpgradeStatus("0","upgraded", 1, 0, Map.of("energy", 100),
                Map.of("energy", 100), 0), null, null);
    }

    protected Event<Game> tickGame() {
        return new Event<>("games." + GAME_ID + ".ticked",
                new Game("0", "0", GAME_ID, "TestGameName", USER_ID,
                1, 1, true, 0, ++this.gameTicks, null));
    }
}
