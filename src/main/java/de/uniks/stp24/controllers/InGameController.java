package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.component.menu.SettingsComponent;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.EventListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

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
    public Button showStorageButton;
    public Button showIslandButton;
    public HBox storageButtonsBox;
    @FXML
    public StackPane overviewContainer;
    @FXML
    ScrollPane mapPane;
    @FXML
    Pane mapGrid;
    @FXML
    StackPane zoomPane;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    public StackPane storageOverviewContainer;

    @Inject
    InGameService inGameService;
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
    @SubComponent
    @Inject
    public OverviewSitesComponent overviewSitesComponent;
    @SubComponent
    @Inject
    public OverviewUpgradeComponent overviewUpgradeComponent;
    @SubComponent
    @Inject
    public StorageOverviewComponent storageOverviewComponent;
    @SubComponent
    @Inject
    public ClockComponent clockComponent;
    @Inject
    IslandsService islandsService;
    @Inject
    Subscriber subscriber;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    EventListener eventListener;
    @Inject
    ResourcesService resourceService;

    public IslandComponent selectedIsland;

    boolean pause = false;
    List<IslandComponent> islandComponentList = new ArrayList<>();

    // todo remove this variables if not needed
    String gameID;
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

        GameStatus gameStatus = inGameService.getGameStatus();
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

        this.subscriber.subscribe(inGameService.loadUpgradePresets(),
                result -> {
                    islandAttributes.setSystemPresets(result);
                });

        this.subscriber.subscribe(empireService.getEmpire(gameID, empireID),
                result -> {
                    islandAttributes.setEmpireDto(result);
                });

        if (!tokenStorage.isSpectator()) {
            createEmpireListener();
        }
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

        clockComponentContainer.getChildren().add(clockComponent);
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
            showStorageButton.setId("showStorageButton");
            showStorageButton.setOnAction(event -> showStorage());
            this.storageButtonsBox.getChildren().addAll(showStorageButton, showIslandButton);
        }
    }

    @FXML
    private void showIslandOverview(ActionEvent actionEvent) {
    }

    @OnRender
    public void createMap() {
        islandsService.getListOfIslands().forEach(
                island -> {
                    IslandComponent tmp = islandsService.createIslandPaneFromDto(island,
                            app.initAndRender(new IslandComponent())
                    );
                    tmp.setLayoutX(tmp.getPosX());
                    tmp.setLayoutY(tmp.getPosY());
                    tmp.rudderImage.setVisible(false);
                    tmp.setInGameController(this);
                    islandComponentList.add(tmp);
                    this.mapGrid.getChildren().add(tmp);
                }
        );
        //todo draw connections
        createButtonsStorage();
    }

    public void showOverview(Island island) {
        islandAttributes.setIsland(island);
        overviewSitesComponent.buildingsComponent.resetPage();
        overviewSitesComponent.buildingsComponent.setGridPane();
        overviewContainer.setVisible(true);
        overviewSitesComponent.sitesContainer.setVisible(true);
        overviewSitesComponent.buildingsButton.setDisable(true);
        inGameService.showOnly(overviewContainer, overviewSitesComponent);
        inGameService.showOnly(overviewSitesComponent.sitesContainer, overviewSitesComponent.buildingsComponent);

        overviewSitesComponent.setOverviewSites();

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

    // assign key S to show storage
    @OnKey(code = KeyCode.S)
    public void showStorage() {
        if (!tokenStorage.isSpectator()) {
            storageOverviewContainer.setVisible(!storageOverviewContainer.isVisible());
        }
    }

    public void createEmpireListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    islandAttributes.setEmpireDto(event.data());
                    System.out.println("Event -> minerals: " + islandAttributes.getAvailableResources().get("minerals") + " alloys: " + islandAttributes.getAvailableResources().get("alloys"));
                    overviewUpgradeComponent.setUpgradeButton();
                },
                error -> System.out.println("errorListener"));
    }
}
