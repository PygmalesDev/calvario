package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.component.game.technology.TechnologyCategoryComponent;
import de.uniks.stp24.component.game.technology.TechnologyOverviewComponent;
import de.uniks.stp24.component.menu.DeleteStructureComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestIslandOverview extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;
    @Spy
    ImageCache imageCache;
    @Spy
    EventService eventService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
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
    GameSystemsApiService gameSystemsApiService;
    @Spy
    PresetsApiService presetsApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    JobsService jobsService;
    @Spy
    JobsApiService jobsApiService;
    @Spy
    ExplanationService explanationService;
    @Spy
    TechnologyService technologyService;
    @Spy
    TimerService timerService;
    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
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
    public BuildingsComponent buildingsComponent;
    @InjectMocks
    EventComponent eventComponent;
    @InjectMocks
    InGameController inGameController;
    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;
    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;
    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;
    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;
    @InjectMocks
    EmpireOverviewComponent empireOverviewComponent;
    @InjectMocks
    VariableService variableService;
    @InjectMocks
    HelpComponent helpComponent;
    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;
    @InjectMocks
    IslandOverviewJobsComponent islandOverviewJobsComponent;
    @InjectMocks
    TechnologyOverviewComponent technologyOverviewComponent;
    @InjectMocks
    TechnologyCategoryComponent technologyCategoryComponent;
    @InjectMocks
    PropertiesJobProgressComponent propertiesJobProgressComponent;
    @InjectMocks
    PropertiesJobProgressComponent siteJobProgress;

    Provider<DistrictComponent> districtComponentProvider = () -> {
        DistrictComponent districtComponent = new DistrictComponent();
        districtComponent.tokenStorage = this.tokenStorage;
        districtComponent.islandAttributeStorage = this.islandAttributeStorage;
        return districtComponent;
    };

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> siteSlots = Map.of("test1", 3, "test2", 3, "test3", 4, "test4", 4);
    Map<String, Integer> sites = Map.of("test1", 2, "test2", 3, "test3", 4, "test4", 4);

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

    EmpireDto empireDto = new EmpireDto(
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
            cost,
            null
    );

    ReadEmpireDto readEmpireDto = new ReadEmpireDto(
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
            "test1",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictAttributes districtPresets2 = new DistrictAttributes(
            "test2",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictAttributes districtPresets3 = new DistrictAttributes(
            "test3",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictAttributes districtPresets4 = new DistrictAttributes(
            "test4",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    ArrayList<BuildingAttributes> buildingAttributes = new ArrayList<>();
    ArrayList<DistrictAttributes> districtAttributes = new ArrayList<>();

    SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);
    ArrayList<BuildingAttributes> buildingPresets = new ArrayList<>();
    ArrayList<DistrictAttributes> districtPresets = new ArrayList<>();

    Island testIsland1;
    Island testIsland2;
    Island testIsland3;
    CreateSystemsDto updatedSystem;
    CreateSystemsDto updatedBuildings;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameController.eventService = this.eventService;
        this.clockComponent.eventService = this.eventService;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameService.setGameStatus(gameStatus);
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.overviewSitesComponent.buildingsComponent.imageCache = this.imageCache;

        // Mock TokenStorage
        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        buildingPresets.add(buildingPreset1);
        buildingPresets.add(buildingPreset2);
        buildingPresets.add(buildingPreset3);
        districtPresets.add(districtPresets1);
        districtPresets.add(districtPresets2);
        districtPresets.add(districtPresets3);
        districtPresets.add(districtPresets4);

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a", "a", "testEmpireID", "testGameID", "testUserID", "testEmpire",
                "a", "a", 1, 2, "a", new String[]{"1"}, cost,
                null))).when(this.empireService).getEmpire(any(), any());

        doReturn(Observable.just(new Game("a", "a", "testGameID", "gameName", "gameOwner", 2, true, 1, 1, null))).when(gamesApiService).getGame(any());
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));

        buildings.add("testBuilding1");
        buildings.add("testBuilding2");
        buildings.add("testBuilding3");
        buildings.add("testBuilding4");
        buildings.add("testBuilding5");
        buildings.add("testBuilding6");

        testIsland1 = new Island(
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

        testIsland2 = new Island(
                null,
                2,
                50,
                50,
                myTestIsland,
                20,
                25,
                2,
                siteSlots,
                sites,
                buildings,
                "1"
                ,
                "explored",
                "TestIsland2"
        );

        testIsland3 = new Island(
                "2",
                2,
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
                "TestIsland3"
        );

        this.islandAttributeStorage.setIsland(testIsland1);

        updatedSystem = new CreateSystemsDto(
                null,
                null,
                testIsland1.id(),
                tokenStorage.getGameId(),
                testIsland1.type(),
                null,
                siteSlots,
                sites,
                testIsland1.resourceCapacity(),
                buildings,
                Upgrade.values()[testIsland1.upgradeLevel() + 1].name(),
                testIsland1.crewCapacity(),
                null,
                (int) testIsland1.posX(),
                (int) testIsland1.posY(),
                tokenStorage.getEmpireId()
        );

        ArrayList<String> buildings1 = new ArrayList<>(buildings);
        buildings1.add("testBuilding3");

        updatedBuildings = new CreateSystemsDto(
                null,
                null,
                testIsland1.id(),
                tokenStorage.getGameId(),
                testIsland1.type(),
                null,
                siteSlots,
                sites,
                testIsland1.resourceCapacity(),
                buildings1,
                Upgrade.values()[testIsland1.upgradeLevel() + 1].name(),
                testIsland1.crewCapacity(),
                null,
                (int) testIsland1.posX(),
                (int) testIsland1.posY(),
                tokenStorage.getEmpireId()
        );
        doReturn(null).when(this.imageCache).get(any());

        this.islandAttributeStorage.systemUpgradeAttributes = systemUpgrades;
        this.islandAttributeStorage.empireDto = empireDto;
        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.overviewSitesComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.overviewUpgradeComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.selectedIsland = new IslandComponent();
        this.resourcesService.islandAttributes = islandAttributeStorage;
        this.resourcesService.tokenStorage = tokenStorage;
        this.resourcesService.empireService = empireService;
        this.inGameController.selectedIsland.rudderImage = new ImageView();
        this.resourcesService.subscriber = subscriber;
        this.inGameController.overviewUpgradeComponent.gameSystemsService = gameSystemsApiService;
        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.selectedIsland.flagPane = new StackPane();

        Map<String, Integer> variablesPresets = new HashMap<>();
        variablesPresets.put("testVar1", 2);
        variablesPresets.put("testVar2", 2);
        variablesPresets.put("testVar3", 2);

        ArrayList<String> traits = new ArrayList<>();
        traits.add("testTrait1");
        traits.add("testTrait2");
        traits.add("testTrait3");

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
        ArrayList<String> effectVar1 = new ArrayList<>();
        ArrayList<String> effectVar2 = new ArrayList<>();
        ArrayList<String> effectVar3 = new ArrayList<>();

        effectVar1.add("effect1");
        effectVar1.add("effect2");

        effectVar2.add("effect3");

        effectVar3.add("effect1");
        effectVar3.add("effect2");
        effectVar3.add("effect3");

        variablesEffect.put("testVar1", effectVar1);
        variablesEffect.put("testVar1", effectVar2);

        ArrayList<Jobs.Job> jobList = new ArrayList<>();

        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.variableService = this.variableService;
        this.inGameController.helpComponent = this.helpComponent;
        this.inGameController.variableService.inGameService.presetsApiService = this.presetsApiService;
        this.inGameController.lobbyService = this.lobbyService;
        this.inGameController.lobbyService.gameMembersApiService = this.gameMembersApiService;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.inGameController.overviewSitesComponent.jobsComponent.jobsService = this.jobsService;
        this.inGameController.clockComponent.timerService = this.timerService;
        this.inGameController.clockComponent.timerService.tokenStorage = this.tokenStorage;
        this.inGameController.clockComponent.timerService.gamesApiService = this.gamesApiService;
        this.inGameController.clockComponent.timerService.subscriber = this.subscriber;
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent = this.technologyCategoryComponent;
        this.inGameController.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;
        this.inGameController.sitePropertiesComponent.siteJobProgress = this.siteJobProgress;
        this.inGameController.technologiesComponent.technologyCategoryComponent.resources = this.gameResourceBundle;
        this.inGameController.technologiesComponent.resources = this.gameResourceBundle;
        this.inGameController.jobsService.tokenStorage = this.tokenStorage;
        this.inGameController.jobsService.jobsApiService = this.jobsApiService;
        this.inGameController.jobsService.subscriber = this.subscriber;
        this.inGameController.jobsService.eventListener = this.eventListener;
        this.inGameController.explanationService = this.explanationService;
        this.inGameController.explanationService.app = this.app;
        this.inGameController.technologiesComponent.technologyService = this.technologyService;
        this.inGameController.overviewSitesComponent.buildingsComponent.imageCache = this.imageCache;
        this.inGameController.overviewSitesComponent.sitesComponent.districtComponentProvider = districtComponentProvider;
        this.inGameController.overviewSitesComponent.jobsComponent.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.detailsComponent.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewUpgradeComponent.explanationService.variableService = this.variableService;
        this.inGameController.overviewUpgradeComponent.explanationService.variableService.technologyService.presetsApiService = this.presetsApiService;

        doReturn(Observable.just(variablesPresets)).when(inGameService).getVariablesPresets();
        doReturn(Observable.just(member)).when(lobbyService).getMember(any(), any());
        doReturn(Observable.just(variablesEffect)).when(inGameService).getVariablesEffects();
        doReturn(Observable.just(jobList)).when(jobsApiService).getEmpireJobs(any(),any());

        buildingAttributes.add(buildingPreset1);
        buildingAttributes.add(buildingPreset2);
        buildingAttributes.add(buildingPreset3);

        districtAttributes.add(districtPresets1);
        districtAttributes.add(districtPresets2);
        districtAttributes.add(districtPresets3);
        districtAttributes.add(districtPresets4);

        this.islandAttributeStorage.buildingsAttributes = this.buildingAttributes;
        this.islandAttributeStorage.districtAttributes = this.districtAttributes;

        this.inGameController.contextMenuButtons = new HBox();
        this.islandsService.isles = islands;
        this.islandsService.tokenStorage = new TokenStorage();
        this.islandsService.gameSystemsService = gameSystemsApiService;

        this.app.show(this.inGameController);

        this.storageOverviewComponent.getStylesheets().clear();
        this.pauseMenuComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
        this.eventComponent.getStylesheets().clear();
        this.storageOverviewComponent.getStylesheets().clear();
        this.overviewSitesComponent.getStylesheets().clear();
        this.overviewUpgradeComponent.getStylesheets().clear();
        this.buildingsComponent.getStylesheets().clear();
        this.sitesComponent.getStylesheets().clear();
        this.detailsComponent.getStylesheets().clear();
        this.deleteStructureComponent.getStylesheets().clear();
        sitePropertiesComponent.getStylesheets().clear();
        buildingsWindowComponent.getStylesheets().clear();
        buildingPropertiesComponent.getStylesheets().clear();
    }

    @Test
    public void testOwnedIsland() {
        doReturn(readEmpireDto).when(islandsService).getEmpire(any());

        //Open island overview of owned Island
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview();
            waitForFxEvents();
        });
        waitForFxEvents();
        assertTrue(this.inGameController.overviewContainer.isVisible());
        waitForFxEvents();
        assertEquals(this.inGameController.overviewSitesComponent.crewCapacity.getText(), String.valueOf(20));
        waitForFxEvents();
        int usedSlots = sitesComponent.getTotalSiteSlots(islandAttributeStorage.getIsland()) +
                islandAttributeStorage.getIsland().buildings().size();
        assertEquals(this.inGameController.overviewSitesComponent.resCapacity.getText(), usedSlots + "/" + islandAttributeStorage.getIsland().resourceCapacity());
        waitForFxEvents();
        assertEquals(this.inGameController.overviewSitesComponent.island_name.getText(), "Plundered Island(Colony)");
        waitForFxEvents();
        assertFalse(this.inGameController.overviewSitesComponent.inputIslandName.isDisable());
        waitForFxEvents();
        assertFalse(this.inGameController.overviewSitesComponent.inputIslandName.isDisable());
        waitForFxEvents();

        //Test function of buttons
        //"Buildings" selected
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
        waitForFxEvents();

        Node prev = lookup("#prev").query();
        Node next = lookup("#next").query();

        //-> Check if building nodes are visible
        ArrayList<Node> buildingNodes = new ArrayList<>(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        for (int i = 0; i < buildingNodes.size() - 2; i++) {
            clickOn(buildingNodes.get(i));
            assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size());
            assertTrue(!prev.isVisible() && !next.isVisible());
        }

        //"Details" selected
        clickOn(this.inGameController.overviewSitesComponent.detailsButton);
        assertTrue(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
        waitForFxEvents();

        Text amount1 = lookup("#amount1").query();
        Text amount2 = lookup("#amount2").query();
        Node showCons = lookup("#showConsumption").query();

        assertEquals("+195", amount1.getText());
        assertEquals("+199", amount2.getText());
        clickOn(showCons);
        waitForFxEvents();
        assertEquals("-265", amount1.getText());
        assertEquals("-275", amount2.getText());

        //"Sites" selected
        clickOn(this.inGameController.overviewSitesComponent.sitesButton);
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
        waitForFxEvents();

        //"Jobs" selected. Enough Resources
        clickOn(this.inGameController.overviewSitesComponent.jobsButton);
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.jobsButton.isDisable());

        //"Upgrades" selected. Enough Resources
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node checkExplored = lookup("#checkExplored").query();
        Node checkColonized = lookup("#checkColonized").query();
        Node checkUpgraded = lookup("#checkUpgraded").query();
        Node checkDeveloped = lookup("#checkDeveloped").query();

        assertTrue(checkExplored.isVisible());
        assertTrue(checkColonized.isVisible());
        assertFalse(checkUpgraded.isVisible());
        assertFalse(checkDeveloped.isVisible());
        waitForFxEvents();
    }

    @Test
    public void closeUpgrade() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview();
            waitForFxEvents();
        });
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node closeButton = lookup("#close").query();
        clickOn(closeButton);
        waitForFxEvents();
        assertFalse(inGameController.overviewContainer.isVisible());
    }

    @Test
    public void closeOverview() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview();
            waitForFxEvents();
        });
        Node closeButton = lookup("#closeButton").query();
        clickOn(closeButton);
        waitForFxEvents();
        assertFalse(inGameController.overviewContainer.isVisible());
    }

    @Test
    public void goBackFromUpgrades() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview();
            waitForFxEvents();
        });
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node backButton = lookup("#backButton").query();
        clickOn(backButton);
        waitForFxEvents();
        assertTrue(inGameController.overviewContainer.isVisible());
    }

    @Test
    public void testUnownedIsland() {
        waitForFxEvents();
        Platform.runLater(() -> {
            islandAttributeStorage.setIsland(testIsland2);
            inGameController.showOverview();
            waitForFxEvents();
        });
        waitForFxEvents();
        assertFalse(this.inGameController.overviewContainer.isVisible());
    }

    @Test
    public void testEnemiesIsland() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview();
            waitForFxEvents();
        });
        waitForFxEvents();
        assertTrue(this.inGameController.overviewContainer.isVisible());
        Node upgradeButton = lookup("#upgradeButton").query();

        assertTrue(upgradeButton.isVisible());

        Node prev = lookup("#prev").query();
        Node next = lookup("#next").query();

        ArrayList<Node> buildingNodes = new ArrayList<>();
        buildingNodes.addAll(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));

        int oldValue = this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size();
        clickOn(buildingNodes.getLast());

        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), oldValue);
        assertTrue(!prev.isVisible() && !next.isVisible());
    }
}
