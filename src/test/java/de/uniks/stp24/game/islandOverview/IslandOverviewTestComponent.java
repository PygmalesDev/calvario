package de.uniks.stp24.game.islandOverview;

import de.uniks.stp24.component.game.DistrictComponent;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class IslandOverviewTestComponent extends IslandOverviewTestInitializer {


    final Provider<DistrictComponent> districtComponentProvider = () -> {
        DistrictComponent districtComponent = new DistrictComponent();
        districtComponent.tokenStorage = this.tokenStorage;
        districtComponent.islandAttributeStorage = this.islandAttributeStorage;
        districtComponent.imageCache = this.imageCache;
        return districtComponent;
    };

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> variablesMarket = new HashMap<>();
    final Map<String,List<SeasonComponent>> _private = new HashMap<>();

    final Map<String, Integer> siteSlots = Map.of("test1", 3, "test2", 3, "test3", 4, "test4", 4);
    final Map<String, Integer> sites = Map.of("test1", 2, "test2", 3, "test3", 4, "test4", 4);

    final IslandType myTestIsland = IslandType.valueOf("uninhabitable_0");
    final ArrayList<String> buildings = new ArrayList();
    final List<Island> islands = new ArrayList<>();

    final Map<String, Integer> cost = Map.of("energy", 3, "fuel", 2);
    final Map<String, Integer> upkeep = Map.of("energy", 3, "fuel", 8);

    final Map<String, Integer> productionBuilding = Map.of("energy", 10, "fuel", 13);
    final Map<String, Integer> productionSites = Map.of("energy", 13, "fuel", 12);
    final Map<String, Integer> consumptionBuilding = Map.of("energy", 5, "fuel", 6);
    final Map<String, Integer> consumptionSites = Map.of("energy", 20, "fuel", 19);


    final UpgradeStatus unexplored = new UpgradeStatus("unexplored", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus explored = new UpgradeStatus("explored", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus colonized = new UpgradeStatus("colonized", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus upgraded = new UpgradeStatus("upgraded", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus developed = new UpgradeStatus("developed", null, 0, 1, cost, upkeep, 1);

    final EmpireDto empireDto = new EmpireDto(
            null,
            null,
            "testEmpireID",
            "123456",
            "testUserID",
            null,
            null,
            null,
            1,
            1,
            null,
            null,
            cost,
            null
    );

    public final ReadEmpireDto readEmpireDto = new ReadEmpireDto(
            null,
            null,
            "testEmpireID",
            "123456",
            "testUserID",
            null,
            null,
            null,
            1,
            1,
            null
    );


    final BuildingAttributes buildingPreset1 = new BuildingAttributes(
            "testBuilding1",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    final BuildingAttributes buildingPreset2 = new BuildingAttributes(
            "testBuilding2",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    final BuildingAttributes buildingPreset3 = new BuildingAttributes(
            "testBuilding3",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    final DistrictAttributes districtPresets1 = new DistrictAttributes(
            "test1",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final DistrictAttributes districtPresets2 = new DistrictAttributes(
            "test2",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final DistrictAttributes districtPresets3 = new DistrictAttributes(
            "test3",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final DistrictAttributes districtPresets4 = new DistrictAttributes(
            "test4",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final ArrayList<BuildingAttributes> buildingAttributes = new ArrayList<>();
    final ArrayList<DistrictAttributes> districtAttributes = new ArrayList<>();

    final SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);

    Island testIsland;

    public void initComponents(){
        initializeComponents();

        this.islandAttributeStorage.systemUpgradeAttributes = systemUpgrades;
        this.islandAttributeStorage.empireDto = empireDto;
        this.resourcesService.setCurrentResources(empireDto.resources());
        this.inGameController.overviewSitesComponent.sitesComponent.districtComponentProvider = districtComponentProvider;

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

        buildings.add("testBuilding1");
        buildings.add("testBuilding2");
        buildings.add("testBuilding3");
        buildings.add("testBuilding4");
        buildings.add("testBuilding5");
        buildings.add("testBuilding6");

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
                "TestIsland1",
          100
        );

        this.islandAttributeStorage.setIsland(testIsland);

        Map<String, Integer> variablesPresets = new HashMap<>();
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

        doReturn(Observable.just(variablesPresets)).when(inGameService).getVariablesPresets();
        doReturn(Observable.just(jobList)).when(jobsApiService).getEmpireJobs(any(), any());
        doReturn(Observable.just(effectSourceParentDto)).when(empireApiService).getEmpireEffect(any(), any());
        doReturn(Observable.empty()).when(marketService).getSeasonalTrades(any(), any());

        buildingAttributes.add(buildingPreset1);
        buildingAttributes.add(buildingPreset2);
        buildingAttributes.add(buildingPreset3);

        districtAttributes.add(districtPresets1);
        districtAttributes.add(districtPresets2);
        districtAttributes.add(districtPresets3);
        districtAttributes.add(districtPresets4);

        this.islandAttributeStorage.buildingsAttributes = this.buildingAttributes;
        this.islandAttributeStorage.districtAttributes = this.districtAttributes;
        this.islandsService.isles = islands;

        this.marketComponent.marketService = this.marketService;
        this.marketService.presetsApiService = this.presetsApiService;
        this.marketComponent.presetsApiService = this.presetsApiService;
        this.marketComponent.subscriber = this.subscriber;
        this.inGameController.marketOverviewComponent = this.marketComponent;
        this.marketService.subscriber = this.subscriber;

        doNothing().when(fleetService).loadGameFleets();
        doNothing().when(fleetService).initializeFleetListeners();
        doNothing().when(contactsComponent).loadEmpireWars();

        this.app.show(this.inGameController);
        clearStyleSheets();
    }

}