package de.uniks.stp24.appTestModules;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.IslandUpgradesJobProgressComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.component.game.DeleteStructureComponent;
import de.uniks.stp24.component.game.technology.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EffectDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Technology;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.controller.Subscriber;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.inject.Inject;
import javax.inject.Provider;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class InGameTestInitializer extends ControllerTest {
    @Mock
    protected MarketService marketService;
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
    TechnologyResearchDetailsComponent technologyResearchDetailsComponent;
    @InjectMocks
    TechnologyEffectDetailsComponent technologyEffectDetailsComponent;
    @Mock
    AnnouncementsService announcementsService;

    Provider<MarketSeasonComponent> marketSeasonComponentProvider = () -> {
        MarketSeasonComponent comp = new MarketSeasonComponent();
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.marketService = this.marketService;
        comp.imageCache = this.imageCache;
        return comp;
    };

    Map<String, Integer> empireResourceStorage = new LinkedHashMap<>() {{
        put("energy", 100);
        put("fuel", 50);
    }};


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

    Trait traitDto = new Trait("traitId", new EffectDto[]{new EffectDto("variable", 0.5, 1.3, 3)}, 3, new String[]{"conflicts"});
    public AggregateResultDto aggregateResult = new AggregateResultDto(
            0,
            empireResources
    );

    public void initializeComponents() {

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
        this.inGameController.overviewUpgradeComponent.gameSystemsService = gameSystemsApiService;
        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.selectedIsland.flagPane = new StackPane();
        this.variableDependencyService.variableService = this.variableService;
        this.inGameController.overviewUpgradeComponent.jobProgressComponent = this.jobProgressComponent;

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

        this.marketService.presetsApiService = this.presetsApiService;
        this.marketService.empireApiService = this.empireApiService;
        this.marketService.subscriber = this.subscriber;

        this.inGameController.contextMenuButtons = new HBox();
        this.islandsService.tokenStorage = new TokenStorage();
        this.islandsService.gameSystemsService = gameSystemsApiService;
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
