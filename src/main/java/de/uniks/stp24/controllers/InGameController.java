package de.uniks.stp24.controllers;


import de.uniks.stp24.component.game.OverviewSitesComponent;
import de.uniks.stp24.component.game.OverviewUpgradeComponent;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.LobbyHostSettingsComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.GamesService;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {
    @FXML
    public StackPane overviewContainer;
    @FXML
    public Pane rudder_pain;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    StackPane storageOverviewContainer;


    @Inject
    InGameService inGameService;
    @Inject
    GamesService gamesService;
    @Inject
    LobbyService lobbyService;
    @Inject
    EmpireService empireService;

    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;
    @SubComponent
    @Inject
    public OverviewSitesComponent overviewSitesComponent;
    @SubComponent
    @Inject
    public OverviewUpgradeComponent overviewUpgradeComponent;
    @Inject
    LobbyHostSettingsComponent lobbyHostSettingsComponent;
    @Inject
    public StorageOverviewComponent storageOverviewComponent;

    String gameID;
    String empireID;


    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();
    public boolean islandClicked = false;

    @Inject
    public InGameController() {
    }

    @OnInit
    public void init() {
        this.subscriber.subscribe(inGameService.getAllIslands(lobbyHostSettingsComponent.gameID),
                islands -> {
                    System.out.println(islands.size());
                });

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
            Boolean paused = (Boolean) propertyChangeEvent.getNewValue();
            if (paused) {
                pauseGame();
            } else {
                resumeGame();
            }
        }
    }

    @OnRender
    public void render() {
        rudder_pain.setVisible(false);
        overviewSitesComponent.setIngameController(this);
        overviewUpgradeComponent.setIngameController(this);
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.getChildren().add(pauseMenuComponent);

        overviewContainer.setVisible(false);
        overviewSitesComponent.setContainer();
        overviewContainer.getChildren().add(overviewSitesComponent);
        overviewContainer.getChildren().add(overviewUpgradeComponent);
        storageOverviewContainer.setVisible(false);
        storageOverviewContainer.getChildren().add(storageOverviewComponent);
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        inGameService.setShowSettings(false);
        inGameService.setPaused(!inGameService.getPaused());
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
        pauseMenuContainer.setVisible(true);
    }

    public void resumeGame() {
        pauseMenuContainer.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
                .removePropertyChangeListener(triple.propertyName(), triple.listener()));
    }

    public void showOverview() {
        islandClicked = true;
        overviewContainer.setVisible(true);
        overviewSitesComponent.sitesContainer.setVisible(true);
        overviewSitesComponent.sitesButton.setDisable(true);
        inGameService.showOnly(overviewContainer, overviewSitesComponent);
        inGameService.showOnly(overviewSitesComponent.sitesContainer, overviewSitesComponent.sitesComponent);
    }

    public void showRudder() {
        if (!islandClicked) {
            rudder_pain.setVisible(true);
        }
    }

    public void unshowRudder() {
        if (!islandClicked) {
            rudder_pain.setVisible(false);
        }

        this.subscriber.dispose();
    }

    public void showStorage() {
        storageOverviewContainer.setVisible(!storageOverviewContainer.isVisible());
    }

    public void showIslandOverview() {


    }
}
