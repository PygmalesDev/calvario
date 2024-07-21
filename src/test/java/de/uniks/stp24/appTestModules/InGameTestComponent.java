package de.uniks.stp24.appTestModules;

import com.sun.scenario.effect.Effect;
import de.uniks.stp24.component.game.DistrictComponent;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import javax.inject.Provider;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class InGameTestComponent extends InGameTestInitializer {
    Button homeIsland;

    Provider<DistrictComponent> districtComponentProvider = () -> {
        DistrictComponent districtComponent = new DistrictComponent();
        districtComponent.tokenStorage = tokenStorage;
        districtComponent.islandAttributeStorage = this.islandAttributeStorage;
        districtComponent.imageCache = this.imageCache;
        return districtComponent;
    };

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> siteSlots = Map.of("energy", 3, "city", 3, "mining", 4, "research", 4);
    Map<String, Integer> sites = Map.of("energy", 2, "city", 3, "mining", 4, "research", 4);

    IslandType myTestIsland = IslandType.valueOf("uninhabitable_0");
    ArrayList<String> buildings = new ArrayList();
    List<Island> islands = new ArrayList<>();

    Map<String, Integer> cost = Map.of("energy", 3, "fuel", 2);
    Map<String, Integer> upkeep = Map.of("energy", 3, "fuel", 8);

    Map<String, Integer> productionBuilding = Map.of("energy", 10, "fuel", 13);
    Map<String, Integer> productionSites = Map.of("energy", 13, "fuel", 12);
    Map<String, Integer> consumptionBuilding = Map.of("energy", 5, "fuel", 6);
    Map<String, Integer> consumptionSites = Map.of("energy", 20, "fuel", 19);


    UpgradeStatus unexplored = new UpgradeStatus("unexplored", null, 0, 1, cost, upkeep, 1);
    UpgradeStatus explored = new UpgradeStatus("explored", null, 0, 1, cost, upkeep, 1);
    UpgradeStatus colonized = new UpgradeStatus("colonized", null, 0, 1, cost, upkeep, 1);
    UpgradeStatus upgraded = new UpgradeStatus("upgraded", null, 0, 1, cost, upkeep, 1);
    UpgradeStatus developed = new UpgradeStatus("developed", null, 0, 1, cost, upkeep, 1);

    Map<String, Integer> empireResourceStorage = new LinkedHashMap<>() {{
        put("energy", 100);
        put("fuel", 50);
    }};

    public AggregateItemDto[] empireResources = new AggregateItemDto[]{
            new AggregateItemDto(
                    "energy",
                    100,
                    20
            ),
            new AggregateItemDto(
                    "fuel",
                    50,
                    -10
            ),
    };

    public AggregateResultDto aggregateResult = new AggregateResultDto(
            0,
            empireResources
    );

    public EmpireDto empireDto = new EmpireDto(
            null,
            null,
            "testEmpireID",
            "testGameID",
            "testUserID",
            null,
            null,
            null,
            1,
            1,
            null,
            null,
            empireResourceStorage,
            null
    );

    public ReadEmpireDto readEmpireDto = new ReadEmpireDto(
            null,
            null,
            "testEmpireID",
            "testGameID",
            "testUserID",
            null,
            null,
            null,
            1,
            1,
            null
    );


    BuildingAttributes buildingPreset1 = new BuildingAttributes(
            "testBuilding1",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    BuildingAttributes buildingPreset2 = new BuildingAttributes(
            "testBuilding2",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    BuildingAttributes buildingPreset3 = new BuildingAttributes(
            "testBuilding3",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    DistrictAttributes districtPresets1 = new DistrictAttributes(
            "energy",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictAttributes districtPresets2 = new DistrictAttributes(
            "city",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictAttributes districtPresets3 = new DistrictAttributes(
            "mining",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictAttributes districtPresets4 = new DistrictAttributes(
            "research",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    ArrayList<BuildingAttributes> buildingAttributes = new ArrayList<>();
    ArrayList<DistrictAttributes> districtAttributes = new ArrayList<>();

    SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);

    Island testIsland;

    SystemDto system;

    Trait traitDto = new Trait("traitId", new EffectDto[]{new EffectDto("variable", 0.5, 1.3, 3)}, 3, new String[]{"conflicts"});

    public void initComponents(){
        initializeComponents();

        this.islandAttributeStorage.systemUpgradeAttributes = systemUpgrades;
        this.islandAttributeStorage.empireDto = empireDto;
        this.inGameController.overviewSitesComponent.sitesComponent.districtComponentProvider = districtComponentProvider;



        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(empireDto)).when(this.empireService).getEmpire(any(), any());
        doReturn(Observable.just(new Game("a", "a", "testGameID", "gameName", "gameOwner", 2, 1, true, 1, 1, null))).when(gamesApiService).getGame(any());
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));

        // Mock getResourceAggregates
        doReturn(Observable.just(aggregateResult)).when(this.empireService).getResourceAggregates(any(), any());

        buildings.add("refinery");
        buildings.add("farm");
        buildings.add("mine");
        buildings.add("not Built");

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

        tokenStorage.setIsland(testIsland);

        system = new SystemDto(
                "",
                "",
                "systemID",
                "testGameID",
                "agriculture",
                "name",
                siteSlots,
                sites,
                25,
                buildings,
                Upgrade.explored,
                20,
                null,
                50,
                50,
                "testEmpireID"
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
        doReturn(Observable.just(member)).when(lobbyService).getMember(any(), any());
        doReturn(Observable.just(variablesEffect)).when(inGameService).getVariablesEffects();
        doReturn(Observable.just(effectSourceParentDto)).when(empireApiService).getEmpireEffect(any(), any());
        doReturn(Observable.just(new BuildingDto("a", 0, cost, productionBuilding, upkeep))).when(resourcesService).getResourcesBuilding(any());

        buildingAttributes.add(buildingPreset1);
        buildingAttributes.add(buildingPreset2);
        buildingAttributes.add(buildingPreset3);

        districtAttributes.add(districtPresets1);
        districtAttributes.add(districtPresets2);
        districtAttributes.add(districtPresets3);
        districtAttributes.add(districtPresets4);

        doReturn(districtAttributes).when(variableDependencyService).createVariableDependencyDistricts();
        islandAttributeStorage.setDistrictAttributes();

        this.islandAttributeStorage.buildingsAttributes = this.buildingAttributes;
        this.islandAttributeStorage.districtAttributes = this.districtAttributes;
        this.islandsService.isles = islands;


        doReturn(Observable.empty()).when(marketService).getVariables();
        doReturn(Observable.empty()).when(marketService).getSeasonalTrades(any(), any());
        doReturn(FXCollections.observableArrayList()).when(announcementsService).getAnnouncements();
        doReturn(FXCollections.observableArrayList()).when(jobsService).getObservableListForSystem(any());
        doReturn(Observable.empty()).when(jobsService).beginJob(any());
        doReturn(testIsland).when(tokenStorage).getIsland();
        doReturn(Observable.empty()).when(gameSystemsApiService).updateIsland(any(), any(), any());

        this.inGameController.buildingPropertiesComponent.certainBuilding = buildingPreset1;

        this.app.show(this.inGameController);
        clearStyleSheets();
    }

    protected void createMap() {
        homeIsland = new Button();
        homeIsland.setLayoutX(500);
        homeIsland.setLayoutY(500);
        homeIsland.setPrefWidth(50);
        homeIsland.setPrefHeight(50);
        homeIsland.setId("homeIsland");
        homeIsland.setOnAction(this::openIslandOverview);
        Platform.runLater(() -> {
            inGameController.mapGrid.getChildren().add(homeIsland);
            waitForFxEvents();
        });
        waitForFxEvents();
    }

    protected void openIslandOverview(ActionEvent actionEvent) {
        this.inGameController.showOverview();
    }

}
