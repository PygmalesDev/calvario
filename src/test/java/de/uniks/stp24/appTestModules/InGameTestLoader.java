package de.uniks.stp24.appTestModules;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.dev.FleetCreationComponent;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.fleetManager.BlueprintsDetailsComponent;
import de.uniks.stp24.component.game.fleetManager.ChangeFleetComponent;
import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import de.uniks.stp24.component.game.fleetManager.NewFleetComponent;
import de.uniks.stp24.component.game.jobs.*;
import de.uniks.stp24.component.game.technology.*;
import de.uniks.stp24.component.game.technology.ResearchJobComponent;
import de.uniks.stp24.component.game.technology.TechnologyCategoryComponent;
import de.uniks.stp24.component.game.technology.TechnologyCategorySubComponent;
import de.uniks.stp24.component.game.technology.TechnologyOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.inject.Provider;

import static org.mockito.Mockito.spy;

public class InGameTestLoader extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
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
    @Mock
    EventListener eventListener;
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
    EmpireApiService empireApiService;
    @Spy
    JobsApiService jobsApiService;
    @Spy
    ExplanationService explanationService;
    @Spy
    TechnologyService technologyService;
    @Spy
    TimerService timerService;
    @Spy
    GameLogicApiService gameLogicApiService;
    @Spy
    VariableDependencyService variableDependencyService;
    @Spy
    AnnouncementsService announcementsService;
    @Spy
    GameStatus gameStatus;
    @Spy
    FleetCoordinationService fleetCoordinationService;
    @Spy
    FleetService fleetService;
    @Spy
    ShipService shipService;
    @Spy
    FleetApiService fleetApiService;

    @InjectMocks
    protected MarketService marketService;
    @InjectMocks
    protected PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    protected StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    protected ClockComponent clockComponent;
    @InjectMocks
    protected OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    protected OverviewUpgradeComponent overviewUpgradeComponent;
    @InjectMocks
    protected IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    protected DetailsComponent detailsComponent;
    @InjectMocks
    protected SitesComponent sitesComponent;
    @InjectMocks
    protected BuildingsComponent buildingsComponent;
    @InjectMocks
    protected EventComponent eventComponent;
    @InjectMocks
    protected InGameController inGameController;
    @InjectMocks
    protected MarketComponent marketComponent;
    @InjectMocks
    protected BuildingPropertiesComponent buildingPropertiesComponent;
    @InjectMocks
    protected SitePropertiesComponent sitePropertiesComponent;
    @InjectMocks
    protected BuildingsWindowComponent buildingsWindowComponent;
    @InjectMocks
    protected DeleteStructureComponent deleteStructureComponent;
    @InjectMocks
    protected EmpireOverviewComponent empireOverviewComponent;
    @InjectMocks
    protected VariableService variableService;
    @InjectMocks
    protected HelpComponent helpComponent;
    @InjectMocks
    protected JobsOverviewComponent jobsOverviewComponent;
    @InjectMocks
    protected IslandOverviewJobsComponent islandOverviewJobsComponent;
    @InjectMocks
    protected PropertiesJobProgressComponent propertiesJobProgressComponent;
    @InjectMocks
    protected PropertiesJobProgressComponent siteJobProgress;
    @InjectMocks
    protected IslandClaimingComponent islandClaimingComponent;
    @InjectMocks
    protected IslandUpgradesJobProgressComponent islandUpgradesJobProgressComponent;
    @InjectMocks
    protected TechnologyOverviewComponent technologyOverviewComponent;
    @InjectMocks
    protected TechnologyCategoryComponent technologyCategoryComponent;
    @InjectMocks
    protected ResearchJobComponent researchJobComponent;
    @InjectMocks
    protected EditGameService editGameService;
    @InjectMocks
    protected JobsService jobsService;
    @InjectMocks
    protected CoolerBubbleComponent coolerBubbleComponent;
    @InjectMocks
    protected FleetManagerComponent fleetManagerComponent;
    @InjectMocks
    protected FleetCreationComponent fleetCreationComponent;
    @InjectMocks
    protected NewFleetComponent newFleetComponent;
    @InjectMocks
    protected ChangeFleetComponent changeFleetComponent;
    @InjectMocks
    protected BlueprintsDetailsComponent blueprintsDetailsComponent;
    @InjectMocks
    protected TechnologyResearchDetailsComponent technologyResearchDetailsComponent;
    @InjectMocks
    protected TechnologyEffectDetailsComponent technologyEffectDetailsComponent;


    final Provider<ClaimingSiteComponent> claimingComponentProvider = () -> {
        var component = new ClaimingSiteComponent();
        component.imageCache = this.imageCache;
        return component;
    };

    final Provider<JobElementComponent> jobElementComponentProvider = () -> {
        JobElementComponent comp = new JobElementComponent();
        comp.gameResourceBundle = gameResourceBundle;
        comp.islandsService = islandsService;
        comp.jobsService = jobsService;
        comp.imageCache = imageCache;
        comp.subscriber = subscriber;
        return comp;
    };

    final Provider<DistrictComponent> districtComponentProvider = () -> {
        DistrictComponent comp = new DistrictComponent();
        comp.islandAttributeStorage = this.islandAttributeStorage;
        comp.tokenStorage = this.tokenStorage;
        comp.imageCache = this.imageCache;
        return comp;
    };

    final Provider<IslandOverviewJobProgressComponent> islandOverviewJobProgressComponentProvider = () -> {
        IslandOverviewJobProgressComponent comp = new IslandOverviewJobProgressComponent();
        comp.islandAttributes = this.islandAttributeStorage;
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.jobsService = this.jobsService;
        comp.subscriber = this.subscriber;
        comp.imageCache = this.imageCache;
        comp.app = this.app;
        return comp;
    };

    final Provider<MarketSeasonComponent> marketSeasonComponentProvider = () -> {
        MarketSeasonComponent comp = new MarketSeasonComponent();
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.marketService = this.marketService;
        comp.imageCache = this.imageCache;
        return comp;
    };

    final Provider<TechnologyCategorySubComponent> technologyCategorySubComponentProvider = () ->
            new TechnologyCategorySubComponent(this.technologyCategoryComponent, this.technologyService,
                    this.app, this.technologiesResourceBundle, this.tokenStorage, this.subscriber, this.variablesResourceBundle, this.technologyEffectDetailsComponent,
                    this.technologyResearchDetailsComponent, this.imageCache);

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.setControllers();
        this.setServices();
    }

    protected void setControllers() {
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent = this.technologyCategoryComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.researchJobComponent = this.researchJobComponent;
        this.inGameController.technologiesComponent.technologyService = this.technologyService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyService = this.technologyService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent = this.technologyResearchDetailsComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyEffectDetailsComponent = this.technologyEffectDetailsComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.resourcesService = this.resourcesService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.resourcesService.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyOverviewComponent = this.technologyOverviewComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.tokenStorage = this.tokenStorage;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.presetsApiService = this.presetsApiService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.technologyService = this.technologyService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.empireApiService = this.empireApiService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.gameLogicApiService = this.gameLogicApiService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.tokenStorage = this.tokenStorage;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.technologyCategoryComponent.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.technologyService.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.subscriber = this.subscriber;
        this.technologyService.gameLogicApiService = this.gameLogicApiService;

        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.coolerBubbleComponent = this.coolerBubbleComponent;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.marketOverviewComponent = this.marketComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.helpComponent = this.helpComponent;

        this.coolerBubbleComponent.announcementsService = this.announcementsService;
        this.coolerBubbleComponent.gameResourceBundle = this.gameResourceBundle;
        this.coolerBubbleComponent.empireService = this.empireService;
        this.coolerBubbleComponent.tokenStorage = this.tokenStorage;
        this.coolerBubbleComponent.jobsService = this.jobsService;
        this.coolerBubbleComponent.subscriber = this.subscriber;

        this.inGameController.marketOverviewComponent.variableService = this.variableService;
        this.variableService.inGameController = this.inGameController;

        this.announcementsService.technologiesResourceBundle = this.technologiesResourceBundle;
        this.announcementsService.gameResourceBundle = this.gameResourceBundle;
        this.announcementsService.islandsService = this.islandsService;
        this.announcementsService.jobsService = this.jobsService;

        this.technologyOverviewComponent.technologyCategoryComponent = this.technologyCategoryComponent;

        this.technologyCategoryComponent.researchJobComponent = this.researchJobComponent;

        this.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.overviewSitesComponent.sitesComponent = this.sitesComponent;

        this.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;

        this.sitePropertiesComponent.siteJobProgress = this.siteJobProgress;

        this.jobsOverviewComponent.jobProvider = this.jobElementComponentProvider;

        this.islandOverviewJobsComponent.progressPaneProvider = this.islandOverviewJobProgressComponentProvider;

        this.overviewUpgradeComponent.jobProgressComponent = this.islandUpgradesJobProgressComponent;

        this.technologyCategoryComponent.provider = this.technologyCategorySubComponentProvider;


        this.fleetManagerComponent.newFleetComponent = this.newFleetComponent;
        this.fleetManagerComponent.changeFleetComponent = this.changeFleetComponent;
        this.fleetManagerComponent.jobsService = this.jobsService;
        this.inGameController.fleetManagerComponent = this.fleetManagerComponent;
        this.inGameController.fleetCreationComponent = this.fleetCreationComponent;
    }

    protected void setServices() {
        this.inGameController.gameSystemsApiService = this.gameSystemsApiService;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.variableService = this.variableService;
        this.inGameController.inGameService = this.inGameService;
        this.inGameController.tokenStorage = this.tokenStorage;
        this.inGameController.eventService = this.eventService;
        this.inGameController.lobbyService = this.lobbyService;
        this.inGameController.jobsService = this.jobsService;
        this.inGameController.subscriber = this.subscriber;

        this.islandClaimingComponent.componentProvider = this.claimingComponentProvider;
        this.islandClaimingComponent.islandAttributes = this.islandAttributeStorage;
        this.islandClaimingComponent.islandsService = this.islandsService;
        this.islandClaimingComponent.jobsService = this.jobsService;
        this.islandClaimingComponent.imageCache = this.imageCache;
        this.islandClaimingComponent.subscriber = this.subscriber;

        this.variableService.technologyService = this.technologyService;
        this.variableService.inGameService = this.inGameService;
        this.variableService.subscriber = this.subscriber;

        this.islandsService.gameSystemsService = this.gameSystemsApiService;
        this.islandsService.tokenStorage = this.tokenStorage;
        this.islandsService.subscriber = this.subscriber;
        this.islandsService.app = this.app;

        this.inGameService.presetsApiService = this.presetsApiService;
        this.inGameService.gameStatus = this.gameStatus;

        this.empireService.empireApiService = this.empireApiService;

        this.lobbyService.gameMembersApiService = this.gameMembersApiService;

        this.editGameService.gamesApiService = this.gamesApiService;

        this.resourcesService.gameSystemsApiService = this.gameSystemsApiService;
        this.resourcesService.islandAttributes = this.islandAttributeStorage;
        this.resourcesService.empireService = this.empireService;
        this.resourcesService.tokenStorage = this.tokenStorage;
        this.resourcesService.subscriber = this.subscriber;

        this.explanationService.variablesResourceBundle = this.variablesResourceBundle;
        this.explanationService.gameResourceBundle = this.gameResourceBundle;
        this.explanationService.variableService = this.variableService;
        this.explanationService.app = this.app;

        this.clockComponent.subscriber = this.subscriber;

        this.islandOverviewJobsComponent.progressPaneProvider = this.islandOverviewJobProgressComponentProvider;
        this.islandOverviewJobsComponent.islandAttributes = this.islandAttributeStorage;

        this.buildingsComponent.islandAttributes = this.islandAttributeStorage;
        this.buildingsComponent.jobsService = this.jobsService;

        this.overviewSitesComponent.islandAttributes = this.islandAttributeStorage;

        this.buildingPropertiesComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.buildingPropertiesComponent.gameSystemsApiService = this.gameSystemsApiService;
        this.buildingPropertiesComponent.resourcesService = this.resourcesService;
        this.buildingPropertiesComponent.islandsService = this.islandsService;
        this.buildingPropertiesComponent.jobsService = this.jobsService;
        this.buildingPropertiesComponent.subscriber = this.subscriber;
        this.buildingPropertiesComponent.imageCache = this.imageCache;
        this.buildingPropertiesComponent.app = this.app;

        this.timerService.gamesApiService = this.gamesApiService;
        this.timerService.tokenStorage = this.tokenStorage;
        this.timerService.subscriber = this.subscriber;

        this.eventComponent.tokenStorage = this.tokenStorage;
        this.eventComponent.subscriber = this.subscriber;

        this.eventService.empireApiService = this.empireApiService;
        this.eventService.tokenStorage = this.tokenStorage;
        this.eventService.subscriber = this.subscriber;

        this.jobsService.jobsApiService = this.jobsApiService;
        this.jobsService.tokenStorage = this.tokenStorage;
        this.jobsService.subscriber = this.subscriber;

        this.storageOverviewComponent.resourcesService = this.resourcesService;
        this.storageOverviewComponent.empireService = this.empireService;
        this.storageOverviewComponent.tokenStorage = this.tokenStorage;
        this.storageOverviewComponent.subscriber = this.subscriber;

        this.marketService.presetsApiService = this.presetsApiService;
        this.marketService.empireApiService = this.empireApiService;
        this.marketService.subscriber = this.subscriber;

        this.marketComponent.marketSeasonComponentProvider = this.marketSeasonComponentProvider;
        this.marketComponent.explanationService = this.explanationService;
        this.marketComponent.presetsApiService = this.presetsApiService;
        this.marketComponent.marketService = this.marketService;
        this.marketComponent.tokenStorage = this.tokenStorage;
        this.marketComponent.subscriber = this.subscriber;
        this.marketComponent.imageCache = this.imageCache;

        this.sitesComponent.districtComponentProvider = this.districtComponentProvider;
        this.sitesComponent.attributeStorage = this.islandAttributeStorage;
        this.sitesComponent.jobsService = this.jobsService;

        this.researchJobComponent.jobsService = this.jobsService;

        this.overviewUpgradeComponent.islandAttributes = this.islandAttributeStorage;
        this.overviewUpgradeComponent.jobsService = this.jobsService;

        this.sitePropertiesComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.sitePropertiesComponent.gameSystemsApiService = this.gameSystemsApiService;
        this.sitePropertiesComponent.gameResourceBundle = this.gameResourceBundle;
        this.sitePropertiesComponent.resourcesService = this.resourcesService;
        this.sitePropertiesComponent.jobsService = this.jobsService;
        this.sitePropertiesComponent.subscriber = this.subscriber;
        this.sitePropertiesComponent.imageCache = this.imageCache;
        this.sitePropertiesComponent.app = this.app;

        this.islandOverviewJobsComponent.jobsService = this.jobsService;

        this.propertiesJobProgressComponent.jobsService = this.jobsService;

        this.siteJobProgress.jobsService = this.jobsService;

        this.jobsOverviewComponent.jobsService = this.jobsService;

        this.technologyService.presetsApiService = this.presetsApiService;
        this.technologyService.empireApiService = this.empireApiService;
        this.technologyService.tokenStorage = this.tokenStorage;
        this.technologyService.subscriber = this.subscriber;

        this.technologyOverviewComponent.technologiesResourceBundle = this.technologiesResourceBundle;

        this.fleetCoordinationService.fleetService = this.fleetService;
        this.fleetCoordinationService.tokenStorage = this.tokenStorage;
        this.fleetService.tokenStorage = this.tokenStorage;
        this.fleetService.fleetApiService = this.fleetApiService;
        this.fleetService.subscriber = this.subscriber;
        this.fleetCoordinationService.subscriber = this.subscriber;
    }

    protected void clearStyles() {
        this.technologyOverviewComponent.getStylesheets().clear();
        this.technologyCategoryComponent.getStylesheets().clear();
        this.buildingPropertiesComponent.getStylesheets().clear();
        this.inGameController.rootPane.getStylesheets().clear();
        this.storageOverviewComponent.getStylesheets().clear();
        this.islandClaimingComponent.getStylesheets().clear();
        this.sitePropertiesComponent.getStylesheets().clear();
        this.overviewSitesComponent.getStylesheets().clear();
        this.jobsOverviewComponent.getStylesheets().clear();
        this.buildingsComponent.getStylesheets().clear();
        this.marketComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
    }
}
