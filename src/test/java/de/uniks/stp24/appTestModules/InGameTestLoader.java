package de.uniks.stp24.appTestModules;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
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
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
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
    JobsService jobsService;
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
    BuildingsComponent buildingsComponent;
    @InjectMocks
    EventComponent eventComponent;
    @InjectMocks
    protected InGameController inGameController;
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

    Provider<ClaimingSiteComponent> claimingComponentProvider = () -> {
        var component = new ClaimingSiteComponent();
        component.imageCache = this.imageCache;
        return component;
    };

    Provider<JobElementComponent> jobElementComponentProvider = () -> {
        JobElementComponent comp = new JobElementComponent();
        comp.islandsService = islandsService;
        comp.imageCache = imageCache;
        comp.jobsService = jobsService;
        comp.subscriber = subscriber;
        comp.gameResourceBundle = gameResourceBundle;
        return comp;
    };

    Provider<IslandOverviewJobProgressComponent> islandOverviewJobProgressComponentProvider = IslandOverviewJobProgressComponent::new;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.setControllers();
        this.setServices();
    }

    protected void setControllers() {
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.helpComponent = this.helpComponent;

        this.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.overviewSitesComponent.sitesComponent = this.sitesComponent;

        this.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;

        this.sitePropertiesComponent.siteJobProgress = this.siteJobProgress;

        this.jobsOverviewComponent.jobProvider = this.jobElementComponentProvider;
        this.islandOverviewJobsComponent.progressPaneProvider = this.islandOverviewJobProgressComponentProvider;

    }

    protected void setServices() {
        this.inGameController.gameSystemsApiService = this.gameSystemsApiService;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.variableService = this.variableService;
        this.inGameController.inGameService = this.inGameService;
        this.inGameController.tokenStorage = this.tokenStorage;
        this.inGameController.eventService = this.eventService;
        this.inGameController.lobbyService = this.lobbyService;
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

        this.empireService.empireApiService = this.empireApiService;

        this.lobbyService.gameMembersApiService = this.gameMembersApiService;

        this.clockComponent.subscriber = this.subscriber;

        this.timerService.gamesApiService = this.gamesApiService;
        this.timerService.tokenStorage = this.tokenStorage;
        this.timerService.subscriber = this.subscriber;

        this.eventComponent.tokenStorage = this.tokenStorage;
        this.eventComponent.subscriber = this.subscriber;

        this.eventService.empireApiService = this.empireApiService;
        this.eventService.tokenStorage = this.tokenStorage;
        this.eventService.subscriber = this.subscriber;

        this.jobsService.jobsApiService = this.jobsApiService;
        this.jobsService.eventListener = this.eventListener;
        this.jobsService.tokenStorage = this.tokenStorage;
        this.jobsService.subscriber = this.subscriber;

        this.sitesComponent.attributeStorage = this.islandAttributeStorage;
    }

    protected void clearStyles() {
        this.inGameController.rootPane.getStylesheets().clear();
        this.islandClaimingComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
    }
}
