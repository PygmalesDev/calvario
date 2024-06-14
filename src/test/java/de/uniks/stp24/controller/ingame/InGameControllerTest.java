package de.uniks.stp24.controller.ingame;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.service.menu.LanguageService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InGameControllerTest extends ControllerTest {
    @InjectMocks
    InGameController inGameController;
    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    SettingsComponent settingsComponent;

    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;

    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);

    @Spy
    GameStatus gameStatus;

    @Spy
    InGameService inGameService;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    Subscriber subscriber = spy(Subscriber.class);

    @Spy
    LanguageService languageService;
//    @Spy
    IslandComponent tmpComp;

    @Spy
    ResourcesService resourcesService;
    @Spy
    GameSystemsApiService gameSystemsApiService;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.islandsService.app = this.app;
        inGameService.setGameStatus(gameStatus);
        islandsService.gameSystemsService = gameSystemsApiService;

        inGameController.mapScrollPane = new ScrollPane();
        inGameController.zoomPane = new StackPane();
        inGameController.mapGrid = new Pane();

        inGameController.zoomPane.getChildren().add(inGameController.mapGrid);
        inGameController.mapScrollPane.setContent(inGameController.mapGrid);


        doReturn(gameStatus).when(this.inGameService).getGameStatus();
        this.app.show(this.inGameController);
        doReturn(null).when(this.app).show("/ingame");
//        doAnswer(show-> {app.show("/ingame");
//            return null;
//        }).when(this.islandsService).retrieveIslands(any());
        islandsService.saveEmpire("empire",new ReadEmpireDto("a","b","empire","game1","user1","name",
          "description","#FFDDEE",2,3,"home"));
        SystemDto[] systems = new SystemDto[3];
        String[] buildings = {"power_plant","mine","farm","research_lab","foundry","factory","refinery"};
        systems[0] = new SystemDto("a","b","system1","game1","agriculture",
          "name",null,null,25,null, Upgrade.unexplored,0,
          Map.of("home",22),1.46,-20.88,null);
        systems[1] = new SystemDto("a","b","system2","game1","energy",
          "name",null,null,16,null, Upgrade.unexplored,0,
          Map.of("home",18),-7.83,-11.04,null);
        systems[2] = new SystemDto("a","b","home","game1","uninhabitable_0", "name",
          Map.of("city",3, "industry", 3, "mining",3, "energy",3, "agriculture",3),
          Map.of("city",3, "industry", 3, "mining",3, "energy",3, "agriculture",3), 22,
          buildings,Upgrade.developed,25,Map.of("system1",22,"system2",18),-5.23,4.23,"empire"
        );
        ;
        doReturn(Observable.just(systems)).when(gameSystemsApiService).getSystems(any());
        Mockito.doCallRealMethod().when(islandsService).retrieveIslands(any());
        Mockito.doCallRealMethod().when(islandsService).getListOfIslands();
        Mockito.doCallRealMethod().when(islandsService).getComponentMap();
        Mockito.doCallRealMethod().when(islandsService).createIslandPaneFromDto(any(Island.class),any(IslandComponent.class));
//        Mockito.doCallRealMethod().when(tmpComp).applyInfo(any(Island.class));
//        Mockito.doCallRealMethod().when(tmpComp).setPosition(any(),any());
//        Mockito.doCallRealMethod().when(tmpComp).setPosition(any(),any());

//        Mockito.doCallRealMethod().when(inGameController).createMap();
//        Mockito.doCallRealMethod().when(islandsService).createIslands(any());
        doReturn(new IslandComponent()).when(app).initAndRender(any(IslandComponent.class));
//        doNothing().when(islandComponent).setFlagImage(any());



    }
    @Test
    public void createIslandData(){

        assertEquals(0,islandsService.getListOfIslands().size());
        islandsService.retrieveIslands("game1");
        gameSystemsApiService.getSystems("game1");
        sleep(1000);

        List<Island> testIsles = islandsService.getListOfIslands();
        Map<String, IslandComponent> compMap = islandsService.getComponentMap();
        List<IslandComponent> compList = new ArrayList<>();

        testIsles.forEach(
          island -> {
              IslandComponent tmp = new IslandComponent();
              tmp.setPosition(island.posX(),island.posY());
              tmp.setFlagImage(island.flagIndex());
              tmp.setLayoutX(tmp.getPosX());
              tmp.setLayoutY(tmp.getPosY());
              compList.add(tmp);
              compMap.put(island.id(), tmp);
          }
        );



    }
}
