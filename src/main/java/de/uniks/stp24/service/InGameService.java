package de.uniks.stp24.service;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import de.uniks.stp24.rest.GameSystemsApiService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InGameService {

    @Inject
    GameStatus gameStatus;
    @Inject
    GameSystemsApiService gameSystemsApiService;

    @Inject
    public InGameService() {

    }

    public void setPaused(Boolean isPaused) {
       gameStatus.setPaused(isPaused);
    }

    public Boolean getPaused() {
        return gameStatus.getPaused();
    }

    public GameStatus getGame() {
        return gameStatus;
    }

    public void setGame(GameStatus gameStatus) {
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

    public Observable<List<Island>> getAllIslands(String gameID) {
        return this.gameSystemsApiService.getAllIslands(gameID);
    }


}
