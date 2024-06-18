package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;


@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {
    public Button showStorageButton;
    public Button showIslandButton;
    public HBox storageButtonsBox;
    @FXML
    public Group group;
    @FXML
    public ScrollPane mapScrollPane;
    @FXML
    public Pane mapGrid;
    @FXML
    public StackPane zoomPane;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    StackPane storageOverviewContainer;

    @Inject
    TimerService timerService;
    @Inject
    InGameService inGameService;
    @Inject
    GamesService gamesService;
    @Inject
    LobbyService lobbyService;
    @Inject
    EmpireService empireService;

    @FXML
    StackPane clockComponentContainer;
    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;
    @Inject
    IslandsService islandsService;
    List<IslandComponent> islandComponentList ;
    Map<String, IslandComponent> islandComponentMap ;
    @SubComponent
    @Inject
    public StorageOverviewComponent storageOverviewComponent;
    @SubComponent
    @Inject
    public ClockComponent clockComponent;

    boolean pause = false;
    double scale = 1.0;
    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();
    // todo remove this variables if not needed
    String gameID;
    String empireID;

    @Inject
    public InGameController() {

    }

    @OnInit
    public void init() {
        GameStatus gameStatus = inGameService.getGameStatus();
        this.empireID = tokenStorage.getEmpireId();
        //Todo: Outprint for Swagger - can be deleted later
        System.out.println("game in ingame: " + tokenStorage.getGameId());
        System.out.println("empire in ingame: " + tokenStorage.getEmpireId());

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
        storageOverviewContainer.setVisible(false);
        storageOverviewContainer.getChildren().add(storageOverviewComponent);
        clockComponentContainer.getChildren().add(clockComponent);
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

    /** created and add buttons for storage and island overview
    * there are problems if they are contained in the fxml
    */
     private void createButtonsStorage() {
        if (!(Objects.nonNull(showIslandButton)&&(Objects.nonNull(showStorageButton)))) {
            showIslandButton = new Button();
            showIslandButton.setPrefHeight(30);
            showIslandButton.setPrefWidth(30);
            showIslandButton.setOnAction(event -> showIslandOverview());
            showStorageButton = new Button();
            showStorageButton.setPrefHeight(30);
            showStorageButton.setPrefWidth(30);
            showStorageButton.setOnAction(event -> showStorage());
        }
        this.storageButtonsBox.getChildren().addAll(showStorageButton, showIslandButton);
    }

    @OnRender
    public void createMap() {
        this.islandComponentList = islandsService.createIslands(islandsService.getListOfIslands());
        this.islandComponentMap = islandsService.getComponentMap();
        mapGrid.setMinSize(islandsService.getMapWidth(), islandsService.getMapHeight());
        islandsService.createLines(this.islandComponentMap).forEach(line -> this.mapGrid.getChildren().add(line));
        this.islandComponentList.forEach(isle -> {
            isle.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showInfo);
            isle.setScaleX(1.25);
            isle.setScaleY(1.25);
            this.mapGrid.getChildren().add(isle);
        });
        createButtonsStorage();
        mapScrollPane.setVvalue(0.5);
        mapScrollPane.setHvalue(0.5);

        /** zoom function working but not perfect!
        * it's necessary to check deltaX and deltaY because 'shiftdown' switches deltas in event
        */
        mapGrid.setOnScroll(event -> {
            if (event.isShiftDown() && (event.getDeltaY() > 0 || event.getDeltaX() > 0 )) {
                scale += 0.1;
                scale = Math.min(scale,1.45);
                event.consume();
            } else if (event.isShiftDown() && (event.getDeltaY() < 0 || event.getDeltaX() <0)){
                scale -= 0.1;
                scale = Math.max(scale,0.85);
                event.consume();
            }
            group.setScaleX(scale);
            group.setScaleY(scale);
        });
        islandsService.mapSites();

    }

    // TODO this could be equivalent to showIslandOverview
    // remove prints
    public void showInfo(MouseEvent event) {
        if (event.getSource() instanceof IslandComponent selected) {
            System.out.println(event.getSource().toString());
            System.out.println("found island: " + selected.getIsland().toString());
            selected.showFlag();
            if (Objects.nonNull((selected.getIsland()).owner())) {
                islandsService.getAllNumberOfSites((selected.getIsland()).owner());
            }
        }
    }

    @OnKey(code = KeyCode.S)
    public void showStorage() {
        storageOverviewContainer.setVisible(!storageOverviewContainer.isVisible());
    }

    public void showIslandOverview() {
        islandsService.getAllNumberOfSites(this.empireID);
    }

    @OnKey(code = KeyCode.SPACE)
    public void resetZoom() {
        scale = 1.0;
        group.setScaleX(scale);
        group.setScaleY(scale);
    }

    public void resetZoomMouse(MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            resetZoom();
        }
    }

    @OnDestroy
    public void destroy() {
        islandComponentList.forEach(IslandComponent::destroy);
        islandComponentList = null;
        islandComponentMap = null;
        islandsService.removeDataForMap();
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
          .removePropertyChangeListener(triple.propertyName(), triple.listener()));
        this.subscriber.dispose();
    }

}
