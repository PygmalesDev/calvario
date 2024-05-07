package de.uniks.stp24.controllers;

import de.uniks.stp24.App;
import de.uniks.stp24.component.PauseMenuComponent;
import de.uniks.stp24.component.SettingsComponent;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import javafx.fxml.FXML;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class InGameController {
    @FXML
    Pane gamePane;

    @FXML
    StackPane pauseMenuContainer;

    @Inject
    App app;

    @SubComponent
    @Inject
    PauseMenuComponent pauseMenuComponent;

    @SubComponent
    @Inject
    SettingsComponent settingsComponent;

    @Inject
    InGameService inGameService;

    private List<GameListenerTriple> gameListenerTriple = new ArrayList<>();

    @Inject
    public InGameController() {

    }

    @OnInit
    public void init() {
        Game game = inGameService.getGame();
        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        game.listeners().addPropertyChangeListener(Game.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(game, callHandlePauseChanged, "PROPERTY_PAUSED"));

        PropertyChangeListener callHandleShowSettings = this::handleShowSettings;
        game.listeners().addPropertyChangeListener(Game.PROPERTY_SETTINGS, callHandleShowSettings);
        this.gameListenerTriple.add(new GameListenerTriple(game, callHandlePauseChanged, "PROPERTY_SETTINGS"));
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
        gamePane.setEffect(new BoxBlur());
        pauseMenuContainer.setVisible(true);
    }

    public void resumeGame() {
        gamePane.setEffect(null);
        pauseMenuContainer.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        this.gameListenerTriple.forEach(triple -> {
            triple.game().listeners().removePropertyChangeListener(triple.propertyName(), triple.listener());
        });
    }
}
