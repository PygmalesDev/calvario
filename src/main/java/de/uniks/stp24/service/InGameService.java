package de.uniks.stp24.service;

import de.uniks.stp24.model.BuildingPresets;
import de.uniks.stp24.model.DistrictPresets;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.SystemUpgrades;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.game.TimerService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class InGameService {

    @Inject
    TimerService timerService;
    @Inject
    GameStatus gameStatus;
    @Inject
    GameSystemsApiService gameSystemsApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    PresetsApiService presetsApiService;

    @Inject
    public InGameService() {

    }

    public Observable<SystemUpgrades> loadUpgradePresets() {
        return presetsApiService.getSystemUpgrades();
    }

    public Observable<ArrayList<BuildingPresets>> loadBuildingPresets() {
        return presetsApiService.getBuildingPresets();
    }

    public Observable<ArrayList<DistrictPresets>> loadDistrictPresets() {
        return presetsApiService.getDistrictPresets();
    }

    public void setPaused(Boolean isPaused) {
       gameStatus.setPaused(isPaused);
    }

    public Boolean getPaused() {
        return gameStatus.getPaused();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setShowSettings(boolean show) {
        gameStatus.setShowSettings(show);
    }

    public void setLanguage(int lang) {
        gameStatus.setLanguage(lang);
    }

    public int getLanguage() {
        return gameStatus.getLanguage();
    }

    public void showOnly(StackPane stackPane, Node nodeToShow) {
        for (Node node : stackPane.getChildren()) {
            node.setVisible(node == nodeToShow);
        }
    }

    public void setTimerService(TimerService timerService) {
        this.timerService = timerService;
    }

    public TimerService getTimerService() {
        return timerService;
    }
}
