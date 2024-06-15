package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.EventComponent;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.event.ActionEvent;
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

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {

    @FXML
    Pane shadow;
    @FXML
    StackPane eventContainer;
    @FXML
    Button showStorageButton;
    @FXML
    Button showIslandButton;
    @FXML
    HBox storageButtonsBox;
    @FXML
    ScrollPane mapPane;
    @FXML
    Pane mapGrid;
    @FXML
    StackPane zoomPane;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    StackPane storageOverviewContainer;
    @FXML
    StackPane clockComponentContainer;

    @Inject
    EventService eventService;
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

    PopupBuilder popUp = new PopupBuilder();


    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;
    @Inject
    IslandsService islandsService;
    List<IslandComponent> islandComponentList = new ArrayList<>();
    Map<String, IslandComponent> islandComponentMap ;

    @SubComponent
    @Inject
    public StorageOverviewComponent storageOverviewComponent;
    @SubComponent
    @Inject
    EventComponent eventComponent;
    @SubComponent
    @Inject
    public ClockComponent clockComponent;

    boolean pause = false;
    private EffectSourceDto event;
    @Inject
    EventListener eventListener;

    String lastUpdate = "";

    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();

    @Inject
    public InGameController() {
    }

    public void createUpdateSeasonListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {

                    if (!lastUpdate.equals(event.data().updatedAt())) {
                        System.out.println("TICKED");
                        eventService.countEventDown();

                        this.event = eventService.getNewRandomEvent();
                        if (this.event != null) {
                            System.out.println("NEW EVENT");
//                            clockComponent.setRandomEventVisible(true);
//                            clockComponent.setRandomEventInfos(this.event);
                            eventComponent.setRandomEventInfos(this.event);
                            eventComponent.show();
                        }
                        lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("Error bei Season: " + error.getMessage()));
    }

    @OnInit
    public void init() {
        GameStatus gameStatus = inGameService.getGameStatus();
        //Todo: Outprint for Swagger - can be deleted later
        System.out.println("game in ingame: " + tokenStorage.getGameId());
        System.out.println("empire in ingame: " + tokenStorage.getEmpireId());

        createUpdateSeasonListener();

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
        eventComponent.setParent(shadow, eventContainer);
        clockComponentContainer.getChildren().add(clockComponent);
        eventContainer.getChildren().add(eventComponent);
        eventContainer.setVisible(false);
        shadow.setVisible(false);
//        eventComponent.show();

        pauseMenuContainer.getChildren().add(pauseMenuComponent);
        storageOverviewContainer.setVisible(false);
        storageOverviewContainer.getChildren().add(storageOverviewComponent);

        eventComponent.setClockComponent(clockComponent);

    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        pause = !pause;
        inGameService.setShowSettings(false);
        inGameService.setPaused(pause);
        if (pause) {
            pauseGame();
        } else {
            resumeGame();
        }
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

    // created and add buttons for storage and island overview
    // there are problems if they are contained in the fxml
    private void createButtonsStorage() {
        if (!(Objects.nonNull(showIslandButton) && (Objects.nonNull(showStorageButton)))) {
            showIslandButton = new Button();
            showIslandButton.setPrefHeight(30);
            showIslandButton.setPrefWidth(30);
            showIslandButton.setOnAction(this::showIslandOverview);
            showStorageButton = new Button();
            showStorageButton.setPrefHeight(30);
            showStorageButton.setPrefWidth(30);
            showStorageButton.setOnAction(event -> showStorage());
            this.storageButtonsBox.getChildren().addAll(showStorageButton, showIslandButton);
        }
    }

    @OnRender
    public void createMap() {

//        app.stage().setFullScreenExitHint("");
//        app.stage().setFullScreen(true);
//        this.mapGrid.setMinSize(islandsService.getMapWidth(), islandsService.getMapHeight());


        this.islandComponentList = islandsService.createIslands(islandsService.getListOfIslands());
        this.islandComponentMap = islandsService.getComponentMap();
        islandsService.createLines(this.islandComponentMap).forEach(line -> this.mapGrid.getChildren().add(line));
        this.islandComponentList.forEach(isle -> {
            isle.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showInfo);
            this.mapGrid.getChildren().add(isle);
        });
        createButtonsStorage();
        mapPane.setVvalue(0.5);
        mapPane.setHvalue(0.5);

    }

    public void showInfo(MouseEvent event) {
        if (event.getSource() instanceof IslandComponent selected) {
            System.out.println(event.getSource().toString());
            System.out.println("found island: " + selected.getIsland().toString());
            selected.showFlag();
        }
    }


    public void showCoordinates() {
        // todo select island to show info
    }

    @OnDestroy
    public void destroy() {
        islandComponentList.forEach(IslandComponent::destroy);
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
                .removePropertyChangeListener(triple.propertyName(), triple.listener()));
        this.subscriber.dispose();
    }

    // assign key S to show storage
    @OnKey(code = KeyCode.S)
    public void showStorage() {
        storageOverviewContainer.setVisible(!storageOverviewContainer.isVisible());
    }

    public void showIslandOverview(ActionEvent actionEvent) {    }

}
