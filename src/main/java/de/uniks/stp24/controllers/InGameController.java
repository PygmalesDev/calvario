package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandsService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;
import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {
    public Button showStorageButton;
    public Button showIslandButton;
    @FXML
    ScrollPane mapPane;
    @FXML
    Pane mapGrid;
    @FXML
    StackPane zoomPane;
    @FXML
    StackPane pauseMenuContainer;
//    @FXML
//    StackPane storageOverviewContainer;
    @Inject
    InGameService inGameService;
    @Inject
    GamesService gamesService;
    @Inject
    LobbyService lobbyService;
    @Inject
    EmpireService empireService;
    @Param("test")
    String test;

    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;
    @Inject
    TokenStorage tokenStorage;

    String gameID;
    @Inject
    IslandsService islandsService;
    List<IslandComponent> islandComponentList = new ArrayList<>();
    @Inject
    Subscriber subscriber;
    boolean pause = false;


//    @SubComponent
//    @Inject
//    public StorageOverviewComponent storageOverviewComponent;

    String empireID;


    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();

    @Inject
    public InGameController() {
    }

    @OnInit
    public void init() {

        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();
        //Todo: Outprint for Swagger - can be deleted later
        System.out.println(this.gameID);
        System.out.println(empireID);


        GameStatus gameStatus = inGameService.getGameStatus();

        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_PAUSED"));

        PropertyChangeListener callHandleShowSettings = this::handleShowSettings;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_SETTINGS, callHandleShowSettings);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_SETTINGS"));

        PropertyChangeListener callHandleLanguageChanged = this::handleLanguageChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_LANGUAGE, callHandleLanguageChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandleLanguageChanged, "PROPERTY_LANGUAGE"));

    }

    private void handleLanguageChanged(PropertyChangeEvent propertyChangeEvent) {
        Locale newLang = propertyChangeEvent.getNewValue().equals(0) ? Locale.GERMAN : Locale.ENGLISH;
    }

    private void handleShowSettings(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            Boolean settings = (Boolean) propertyChangeEvent.getNewValue();
            if (settings) {
                showSettings();
            } else {
                unShowSettings();
            }
        }
    }

    private void handlePauseChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
//            Boolean paused = (Boolean) propertyChangeEvent.getNewValue();
            pause = (Boolean) propertyChangeEvent.getNewValue();
            if (pause) {
                pauseGame();
            } else {
                resumeGame();
            }
        }
    }

    @OnRender
    public void render() {
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.getChildren().add(pauseMenuComponent);

//        storageOverviewContainer.setVisible(false);
//        storageOverviewContainer.getChildren().add(storageOverviewComponent);
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        pause = !pause;
        inGameService.setShowSettings(false);
        inGameService.setPaused(pause);
        if (pause) {pauseGame();}
        else {resumeGame();}
    }

    public void showSettings() {
        pauseMenuContainer.getChildren().remove(pauseMenuComponent);
        pauseMenuContainer.getChildren().add(settingsComponent);
    }

    public void unShowSettings() {
        pauseMenuContainer.getChildren().remove(settingsComponent);
        pauseMenuContainer.getChildren().add(pauseMenuComponent);
    }
    public void pauseGame() {
        pauseMenuContainer.setVisible(pause);
    }

    public void resumeGame() {
        pauseMenuContainer.setVisible(pause);
    }


    @OnRender
    public void createMap()  {

        // sea should be inserted using css -> remove this line
        this.mapGrid.setStyle("-fx-background-image: url('/de/uniks/stp24/icons/sea.png')");

       /* islandsService.getListOfIslands().forEach(
          island -> {
              IslandComponent tmp = islandsService.createIslandPaneFromDto(island,
                app.initAndRender(new IslandComponent())
              );
              tmp.setLayoutX(tmp.getPosX());
              tmp.setLayoutY(tmp.getPosY());
              islandComponentList.add(tmp);
              this.mapGrid.getChildren().add(tmp);
          }
        );*/

        //todo draw connections

    }

    public void showCoordinates(MouseEvent mouseEvent) {
        // todo select island to show info
    }

    @OnDestroy
    public void destroy() {
        islandComponentList.forEach(IslandComponent::destroy);
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
          .removePropertyChangeListener(triple.propertyName(), triple.listener()));
        this.subscriber.dispose();
    }

//    public void showStorage() {
//        storageOverviewContainer.setVisible(!storageOverviewContainer.isVisible());
//    }

    public void showIslandOverview() {


    }
}
