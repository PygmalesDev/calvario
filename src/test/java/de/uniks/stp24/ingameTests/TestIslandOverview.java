package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.CreateSystemsDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.*;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
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

import java.util.*;

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
    TimerService timerService;
    @Spy
    EventService eventService;
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
    GameSystemsApiService gameSystemsApiService;
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
    EventComponent eventComponent;
    @InjectMocks
    InGameController inGameController;


    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> siteSlots = Map.of("test1", 3, "test2", 3, "test3", 4, "test4", 4);
    Map<String, Integer> sites = Map.of("test1", 2, "test2", 3, "test3", 4, "test4", 4);

    IslandType myTestIsland = IslandType.valueOf("uninhabitable_0");
    ArrayList<String> buildings = new ArrayList();
    List<Island> islands = new ArrayList<>();

    Map<String, Integer> cost = Map.of("energy", 3, "fuel", 2);
    Map<String, Integer> upkeep = Map.of("energy", 3, "fuel", 8);
    Map<String, Integer> resAfterUpgrade = Map.of("energy", 0, "fuel", 0);

    Map<String, Integer> productionBuilding = Map.of("energy", 10, "fuel", 13);
    Map<String, Integer> productionSites = Map.of("energy", 13, "fuel", 12);
    Map<String, Integer> consumptionBuilding = Map.of("energy", 5, "fuel", 6);
    Map<String, Integer> consumptionSites = Map.of("energy", 20, "fuel", 19);


    UpgradeStatus unexplored = new UpgradeStatus("unexplored", 1, cost, upkeep, 1);
    UpgradeStatus explored = new UpgradeStatus("explored", 1, cost, upkeep, 1);
    UpgradeStatus colonized = new UpgradeStatus("colonized", 1, cost, upkeep, 1);
    UpgradeStatus upgraded = new UpgradeStatus("upgraded", 1, cost, upkeep, 1);
    UpgradeStatus developed = new UpgradeStatus("developed", 1, cost, upkeep, 1);

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

    EmpireDto empireDtoAfterUpgrade = new EmpireDto(
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
            resAfterUpgrade,
            null
    );

    BuildingPresets buildingPreset1 = new BuildingPresets(
            "testBuilding1",
            null,
            consumptionBuilding,
            productionBuilding
    );

    BuildingPresets buildingPreset2 = new BuildingPresets(
            "testBuilding2",
            null,
            consumptionBuilding,
            productionBuilding
    );

    BuildingPresets buildingPreset3 = new BuildingPresets(
            "testBuilding3",
            null,
            consumptionBuilding,
            productionBuilding
    );

    DistrictPresets districtPresets1 = new DistrictPresets(
            "test1",
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictPresets districtPresets2 = new DistrictPresets(
            "test2",
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictPresets districtPresets3 = new DistrictPresets(
            "test3",
            null,
            null,
            consumptionSites,
            productionSites
    );

    DistrictPresets districtPresets4 = new DistrictPresets(
            "test4",
            null,
            null,
            consumptionSites,
            productionSites
    );

    SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);
    ArrayList<BuildingPresets> buildingPresets = new ArrayList<>();
    ArrayList<DistrictPresets> districtPresets = new ArrayList<>();

    Island testIsland1;
    Island testIsland2;
    Island testIsland3;
    CreateSystemsDto updatedSystem;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.eventComponent.tokenStorage = this.tokenStorage;
        this.clockComponent.tokenStorage = this.tokenStorage;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
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

        Game game = new Game("a", "a", "testGameID", "gameName", "gameOwner", true, 1, 1, null);
        doReturn(Observable.just(game)).when(gamesApiService).getGame(any());
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));
        doReturn(Observable.just(systemUpgrades)).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(buildingPresets)).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(districtPresets)).when(inGameService).loadDistrictPresets();


        doReturn(Observable.just(new Event<>("games.testGameID.ticked",game))).when(this.eventListener).listen(eq("games.testGameID.ticked"), eq(Game.class));
        doReturn(Observable.just(new Event<>("games.testGameID.updated",game))).when(this.eventListener).listen(eq("games.testGameID.updated"), eq(Game.class));


        buildings.add("testBuilding1");
        buildings.add("testBuilding2");
        buildings.add("testBuilding3");

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
                "1"
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
                "1"
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

        this.islandAttributeStorage.systemPresets = systemUpgrades;
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

        this.inGameController.storageButtonsBox = new HBox();
        this.islandsService.isles = islands;

        this.app.show(this.inGameController);

        this.storageOverviewComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
        this.pauseMenuComponent.getStylesheets().clear();
        this.settingsComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
        this.eventComponent.getStylesheets().clear();
        this.storageOverviewComponent.getStylesheets().clear();
        this.overviewSitesComponent.getStylesheets().clear();
        this.overviewUpgradeComponent.getStylesheets().clear();
        this.buildingsComponent.getStylesheets().clear();
        this.sitesComponent.getStylesheets().clear();
        this.detailsComponent.getStylesheets().clear();
    }

    @Test
    public void testOwnedIsland() {
        doReturn(Observable.just(empireDtoAfterUpgrade)).when(empireService).updateEmpire(any(), any(), any());
        doReturn(Observable.just(updatedSystem)).when(gameSystemsApiService).updateIsland(any(), any(), any());
        doReturn(readEmpireDto).when(islandsService).getEmpire(any());

        //Open island overview of owned Island
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview(testIsland1);
            waitForFxEvents();
        });
        waitForFxEvents();
        assertTrue(this.inGameController.overviewContainer.isVisible());
        waitForFxEvents();
        assertEquals(this.inGameController.overviewSitesComponent.crewCapacity.getText(), String.valueOf(20));
        waitForFxEvents();
        int usedSlots = sitesComponent.getTotalSiteSlots(islandAttributeStorage.getIsland()) +
                islandAttributeStorage.getIsland().buildings().size();
        assertEquals(this.inGameController.overviewSitesComponent.resCapacity.getText(),  usedSlots + "/" + islandAttributeStorage.getIsland().resourceCapacity());
        waitForFxEvents();
        assertEquals("Plundered Island(Colony)",this.inGameController.overviewSitesComponent.island_name.getText());
        waitForFxEvents();
        assertFalse(this.inGameController.overviewSitesComponent.inputIslandName.isDisable());
        waitForFxEvents();
        assertFalse(this.inGameController.overviewSitesComponent.inputIslandName.isDisable());
        waitForFxEvents();
        assertEquals("+100.0% more crew mates",this.inGameController.overviewSitesComponent.island_inf.getText());


        //Test function of buttons
        //"Buildings" selected
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        waitForFxEvents();

        Node prev = lookup("#prev").query();
        Node next = lookup("#next").query();

        //-> Check functions buildings
        ArrayList<Node> buildingNodes = new ArrayList<>(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));

        for (int i = 0; i < buildingNodes.size() - 2; i++) {
            clickOn(buildingNodes.get(i));
            assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size());
            assertTrue(!prev.isVisible() && !next.isVisible());
        }

        clickOn(buildingNodes.getLast());
        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size() + 1);
        assertTrue(!prev.isVisible() && !next.isVisible());

        buildingNodes.clear();
        buildingNodes.addAll(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        clickOn(buildingNodes.getLast());
        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size() + 1);
        assertTrue(!prev.isVisible() && !next.isVisible());

        buildingNodes.clear();
        buildingNodes.addAll(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        clickOn(buildingNodes.getLast());
        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size() + 1);
        assertTrue(!prev.isVisible() && !next.isVisible());

        buildingNodes.clear();
        buildingNodes.addAll(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        clickOn(buildingNodes.getLast());
        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size() + 1);
        assertTrue(!prev.isVisible() && !next.isVisible());

        buildingNodes.clear();
        buildingNodes.addAll(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        clickOn(buildingNodes.getLast());
        assertTrue(!prev.isVisible() && next.isVisible());
        waitForFxEvents();

        clickOn(next);
        buildingNodes.clear();
        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), 1);
        assertTrue(prev.isVisible() && next.isVisible());
        waitForFxEvents();
        clickOn(prev);
        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), 8);
        waitForFxEvents();

        //"Details" selected
        clickOn(this.inGameController.overviewSitesComponent.detailsButton);
        assertTrue(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        waitForFxEvents();

        Node prodNode = lookup("#sumProduction").query();
        Node consNode = lookup("#sumConsumption").query();

        if (prodNode instanceof ListView<?> && consNode instanceof ListView<?>) {
            @SuppressWarnings("unchecked")
            ListView<String> prodList = (ListView<String>) prodNode;
            @SuppressWarnings("unchecked")
            ListView<String> consList = (ListView<String>) consNode;

            ObservableList<String> prodItems = prodList.getItems();
            ObservableList<String> consItems = consList.getItems();

            for (String item : prodItems) {
                if (item.contains("fuel")) {
                    assertTrue(item.contains("195"));
                } else if (item.contains("energy")) {
                    assertTrue(item.contains("199"));
                }
            }

            for (String item : consItems) {
                if (item.contains("fuel")) {
                    assertTrue(item.contains("265"));
                } else if (item.contains("energy")) {
                    assertTrue(item.contains("275"));
                }
            }
        }

        //"Sites" selected
        clickOn(this.inGameController.overviewSitesComponent.sitesButton);
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        waitForFxEvents();
        //-> Check Sites information
        assertEquals(this.inGameController.overviewSitesComponent.sitesComponent.sitesBox.getChildren().size(), siteSlots.size());
        ArrayList<Text> sitesName = new ArrayList<>();
        ArrayList<String> sitesInf = new ArrayList<>();

        this.inGameController.overviewSitesComponent.sitesComponent.sitesBox.lookupAll("#districtName").
                forEach(node ->
                {
                    Text textNode = (Text) node;
                    sitesName.add(textNode);
                    assertTrue(sites.containsKey(textNode.getText()));
                });

        this.inGameController.overviewSitesComponent.sitesComponent.sitesBox.lookupAll("#districtCapacity").
                forEach(node ->
                {
                    Text textNode = (Text) node;
                    sitesInf.add(textNode.getText());
                });

        for (Text name : sitesName) {
            Text capacity = new Text();
            capacity.setText(sites.get(name.getText()) + "/" + siteSlots.get(name.getText()));

            if (!sitesInf.contains(capacity.getText())) {
                fail("Overview does not contain the right site information");
            }
        }
        waitForFxEvents();

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

        Node confirmButton = lookup("#confirmUpgrade").query();
        assertTrue(confirmButton.getStyle().contains("-fx-background-color: green;"));
        clickOn(confirmButton);
        waitForFxEvents();
        Text inf = lookup("#island_inf").queryText();
        assertEquals("+100.0% more crew mates",inf.getText());
        clickOn(upgradeButton);
        assertTrue(confirmButton.getStyle().contains("-fx-background-color: black;"));

        assertTrue(checkExplored.isVisible());
        assertTrue(checkColonized.isVisible());
        assertTrue(checkUpgraded.isVisible());
        assertFalse(checkDeveloped.isVisible());

        waitForFxEvents();
    }

    @Test
    public void closeUpgrade() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview(testIsland1);
            waitForFxEvents();
        });
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node closeButton = lookup("#closeButton").query();
        clickOn(closeButton);
        waitForFxEvents();
        assertFalse(inGameController.overviewContainer.isVisible());
    }

    @Test
    public void closeOverview() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview(testIsland1);
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
            inGameController.showOverview(testIsland1);
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
            inGameController.showOverview(testIsland2);
            waitForFxEvents();
        });
        waitForFxEvents();
        assertFalse(this.inGameController.overviewContainer.isVisible());
    }

    @Test
    public void testEnemiesIsland() {
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showOverview(testIsland3);
            waitForFxEvents();
        });
        waitForFxEvents();
        assertTrue(this.inGameController.overviewContainer.isVisible());
        Node upgradeButton = lookup("#upgradeButton").query();
        assertFalse(upgradeButton.isVisible());

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
