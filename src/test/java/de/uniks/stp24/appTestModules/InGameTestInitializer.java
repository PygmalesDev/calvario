package de.uniks.stp24.appTestModules;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.fleetManager.ChangeFleetComponent;
import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import de.uniks.stp24.component.game.fleetManager.NewFleetComponent;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.IslandUpgradesJobProgressComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.component.game.technology.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.fulib.fx.controller.Subscriber;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.inject.Provider;

import static org.mockito.Mockito.spy;

public class InGameTestInitializer extends ControllerTest {
    @Spy
    MarketService marketService;
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
    final
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
    EmpireService empireService;
    @Spy
    public GameSystemsApiService gameSystemsApiService;
    @Spy
    PresetsApiService presetsApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    EmpireApiService empireApiService;
    @Mock
    JobsService jobsService;
    @Mock
    TechnologyService technologyService;
    @InjectMocks
    TechnologyOverviewComponent technologyOverviewComponent;
    @Spy
    JobsApiService jobsApiService;
    @Spy
    ExplanationService explanationService;
    @InjectMocks
    TechnologyCategoryComponent technologyCategoryComponent;
    @Spy
    TimerService timerService;
    @Spy
    GameLogicApiService gameLogicApiService;
    @Spy
    VariableDependencyService variableDependencyService;
    @Spy
    FleetCoordinationService fleetCoordinationService;
    @Spy
    FleetService fleetService;
    @Spy
    ShipService shipService;
    @Spy
    FleetApiService fleetApiService;

    @InjectMocks
    ResearchJobComponent researchJobComponent;
    @InjectMocks
    IslandUpgradesJobProgressComponent jobProgressComponent;
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
    public IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    DetailsComponent detailsComponent;
    @InjectMocks
    public SitesComponent sitesComponent;
    @InjectMocks
    BuildingsComponent buildingsComponent;
    @InjectMocks
    EventComponent eventComponent;
    @InjectMocks
    public InGameController inGameController;
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
    PropertiesJobProgressComponent propertiesJobProgressComponent;
    @InjectMocks
    PropertiesJobProgressComponent siteJobProgress;
    @InjectMocks
    IslandClaimingComponent islandClaimingComponent;
    @InjectMocks
    protected MarketComponent marketComponent;
    @InjectMocks
    CoolerBubbleComponent coolerBubbleComponent;
    @InjectMocks
    FleetManagerComponent fleetManagerComponent;
    @InjectMocks
    NewFleetComponent newFleetComponent;
    @InjectMocks
    ChangeFleetComponent changeFleetComponent;
    @InjectMocks
    IslandTravelComponent islandTravelComponent;
    @InjectMocks
    TechnologyResearchDetailsComponent technologyResearchDetailsComponent;
    @InjectMocks
    TechnologyEffectDetailsComponent technologyEffectDetailsComponent;

    @Mock
    AnnouncementsService announcementsService;

    final Provider<MarketSeasonComponent> marketSeasonComponentProvider = () -> {
        MarketSeasonComponent comp = new MarketSeasonComponent();
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.marketService = this.marketService;
        comp.imageCache = this.imageCache;
        return comp;
    };

    public void initializeComponents() {
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent = this.technologyCategoryComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.researchJobComponent = this.researchJobComponent;
        this.inGameController.coolerBubbleComponent = this.coolerBubbleComponent;
        this.inGameController.coolerBubbleComponent.subscriber = this.coolerBubbleComponent.subscriber;
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.buildingsWindowComponent.tokenStorage = this.tokenStorage;
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
        this.inGameController.deleteStructureComponent.tokenStorage = this.tokenStorage;
        this.inGameController.deleteStructureComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent.imageCache = this.imageCache;

        this.inGameController.marketOverviewComponent = this.marketComponent;
        this.inGameController.marketOverviewComponent.variableService = this.variableService;
        this.marketComponent.marketSeasonComponentProvider = this.marketSeasonComponentProvider;
        this.marketComponent.explanationService = this.explanationService;
        this.marketComponent.presetsApiService = this.presetsApiService;
        this.marketComponent.marketService = this.marketService;
        this.marketComponent.tokenStorage = this.tokenStorage;
        this.marketComponent.subscriber = this.subscriber;
        this.marketComponent.imageCache = this.imageCache;

        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.overviewSitesComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.overviewUpgradeComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.selectedIsland = new IslandComponent();
        this.resourcesService.islandAttributes = islandAttributeStorage;
        this.resourcesService.tokenStorage = tokenStorage;
        this.resourcesService.empireService = empireService;
        this.inGameController.selectedIsland.rudderImage = new ImageView();
        this.resourcesService.subscriber = subscriber;
        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.selectedIsland.flagPane = new StackPane();
        this.variableDependencyService.variableService = this.variableService;
        this.inGameController.overviewUpgradeComponent.jobProgressComponent = this.jobProgressComponent;
        this.inGameController.islandTravelComponent = this.islandTravelComponent;

        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.variableService = this.variableService;
        this.inGameController.helpComponent = this.helpComponent;
        this.inGameController.variableService.inGameService.presetsApiService = this.presetsApiService;
        this.inGameController.lobbyService = this.lobbyService;
        this.inGameController.lobbyService.gameMembersApiService = this.gameMembersApiService;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.inGameController.overviewUpgradeComponent.jobsService = this.jobsService;
        this.inGameController.overviewSitesComponent.jobsComponent.jobsService = this.jobsService;
        this.inGameController.clockComponent.timerService = this.timerService;
        this.inGameController.clockComponent.timerService.tokenStorage = this.tokenStorage;
        this.inGameController.clockComponent.timerService.gamesApiService = this.gamesApiService;
        this.inGameController.clockComponent.timerService.subscriber = this.subscriber;
        this.inGameController.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;
        this.inGameController.buildingPropertiesComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.inGameController.buildingPropertiesComponent.tokenStorage = this.tokenStorage;
        this.inGameController.sitePropertiesComponent.siteJobProgress = this.siteJobProgress;
        this.inGameController.sitePropertiesComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.inGameController.sitePropertiesComponent.tokenStorage = this.tokenStorage;
        this.inGameController.jobsService = this.jobsService;
        this.inGameController.jobsService.tokenStorage = this.tokenStorage;
        this.inGameController.jobsService.jobsApiService = this.jobsApiService;
        this.inGameController.jobsService.subscriber = this.subscriber;
        this.inGameController.jobsService.eventListener = this.eventListener;
        this.inGameController.explanationService = this.explanationService;
        this.inGameController.explanationService.app = this.app;
        this.inGameController.overviewSitesComponent.buildingsComponent.imageCache = this.imageCache;
        this.inGameController.overviewSitesComponent.buildingsComponent.tokenStorage = this.tokenStorage;
        this.inGameController.overviewSitesComponent.jobsComponent.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.detailsComponent.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewUpgradeComponent.explanationService.variableService = this.variableService;
        this.inGameController.overviewUpgradeComponent.explanationService.variableService.technologyService.presetsApiService = this.presetsApiService;
        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.resourcesService.gameSystemsApiService = this.gameSystemsApiService;

        this.technologyCategoryComponent.technologyResearchDetailsComponent = this.technologyResearchDetailsComponent;
        this.technologyCategoryComponent.technologyEffectDetailsComponent = this.technologyEffectDetailsComponent;

        this.marketService.presetsApiService = this.presetsApiService;
        this.marketService.empireApiService = this.empireApiService;
        this.marketService.subscriber = this.subscriber;

        this.inGameController.contextMenuButtons = new HBox();
        this.islandsService.tokenStorage = new TokenStorage();
        this.islandsService.gameSystemsService = gameSystemsApiService;

        this.inGameController.fleetManagerComponent = this.fleetManagerComponent;
        this.inGameController.fleetManagerComponent.newFleetComponent = this.newFleetComponent;
        this.inGameController.fleetManagerComponent.changeFleetComponent = this.changeFleetComponent;
        this.fleetCoordinationService.fleetService = this.fleetService;
        this.fleetCoordinationService.tokenStorage = this.tokenStorage;
        this.fleetService.tokenStorage = this.tokenStorage;
        this.fleetService.fleetApiService = this.fleetApiService;
        this.fleetService.subscriber = this.subscriber;
        this.fleetCoordinationService.subscriber = this.subscriber;
        this.fleetCoordinationService.jobsService = this.jobsService;
        this.fleetCoordinationService.shipService = this.shipService;
        this.fleetCoordinationService.timerService = this.timerService;

        this.timerService.subscriber = this.subscriber;
        this.timerService.eventListener = this.eventListener;
    }

    public void clearStyleSheets(){
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
        this.sitePropertiesComponent.getStylesheets().clear();
        this.buildingsWindowComponent.getStylesheets().clear();
        this.buildingPropertiesComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.getStylesheets().clear();
        this.inGameController.overviewUpgradeComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.detailsComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.buildingsComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.sitesComponent.getStylesheets().clear();
        this.inGameController.marketOverviewComponent.getStylesheets().clear();
        this.inGameController.islandClaimingComponent.getStylesheets().clear();
    }
}
