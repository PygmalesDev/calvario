package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.LanguageService;
import de.uniks.stp24.service.PrefService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

@Controller
public class InGameController {
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    StackPane storageOverviewContainer;

    @Inject
    App app;
    @Inject
    PrefService prefService;

    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;

    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;

    @SubComponent
    @Inject
    public StorageOverviewComponent storageOverviewComponent;


    @Inject
    InGameService inGameService;
    @Inject
    LanguageService languageService;
    @Inject
    @Resource
    ResourceBundle resources;

    private List<GameListenerTriple> gameListenerTriple = new ArrayList<>();


    @Inject
    public InGameController() {
    }

    @OnInit
    public void init() {
        GameStatus gameStatus = inGameService.getGame();
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
        //resources = languageService.setLocale(newLang);
        //app.refresh();
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
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.getChildren().add(pauseMenuComponent);

        storageOverviewContainer.setVisible(false);
        storageOverviewContainer.getChildren().add(storageOverviewComponent);
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        inGameService.setShowSettings(false);
        if (inGameService.getPaused()) {
            inGameService.setPaused(false);
        } else {
            inGameService.setPaused(true);
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
        pauseMenuContainer.setVisible(true);
    }

    public void resumeGame() {
        pauseMenuContainer.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        this.gameListenerTriple.forEach(triple -> {
            triple.game().listeners().removePropertyChangeListener(triple.propertyName(), triple.listener());
        });
    }

    public void showStorage() {
        storageOverviewContainer.setVisible(true);
    }

    public void showIslandOverview() {
    }
}
