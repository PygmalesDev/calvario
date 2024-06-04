package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.*;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.PopupBuilder;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
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
import java.util.Locale;
import java.util.Objects;

@Controller
public class InGameController extends BasicController {
    @FXML
    StackPane siteProperties;
    @FXML
    StackPane buildingProperties;
    @FXML
    StackPane buildingsWindow;
    @FXML
    StackPane overviewContainer;
    @FXML
    StackPane pauseMenuContainer;
    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public SettingsComponent settingsComponent;

    @SubComponent
    @Inject
    public BuildingPropertiesComponent buildingPropertiesComponent;

    @SubComponent
    @Inject
    public BuildingsWindowComponent buildingsWindowComponent;

    @SubComponent
    @Inject
    public SitePropertiesComponent sitePropertiesComponent;

    @Inject
    InGameService inGameService;

    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();

    PopupBuilder popupBuildingProperties = new PopupBuilder();
    PopupBuilder popupBuildingWindow = new PopupBuilder();

    PopupBuilder popupSiteProperties= new PopupBuilder();

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
        //buildingProperties.setVisible(true);
        //buildingProperties.getChildren().add(buildingPropertiesComponent);
        popupBuildingWindow.showPopup(buildingsWindow, buildingsWindowComponent);
        popupBuildingProperties.showPopup(buildingProperties, buildingPropertiesComponent);
        popupSiteProperties.showPopup(siteProperties, sitePropertiesComponent);

        pauseMenuContainer.setMouseTransparent(true);
        pauseMenuContainer.setVisible(false);
        //pauseMenuContainer.getChildren().add(pauseMenuComponent);
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

    }
}
