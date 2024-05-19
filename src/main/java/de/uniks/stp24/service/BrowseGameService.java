package de.uniks.stp24.service;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.User;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class BrowseGameService {
    @Inject
    GamesApiService gamesApiService;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    PrefService prefService;

    private Game game;

    private boolean blurStatus;

    @Inject
    public BrowseGameService() {
    }

    public void setGame(Game game){
        this.game = game;
    }

    public Game getGame(){
        return this.game;
    }

    public void resetSelectedGame(){
        this.game = null;
    }

    public void handleGameSelection(Game game) {
        this.game = game;
    }

    //Sort list of games in ListView. Recent games on top of the list.
    public ObservableList<Game> sortGames(List<Game> games) {
        ObservableList<Game> sortedGames = FXCollections.observableArrayList();
        for (int i = games.size() - 1; i >= 0; i--) {
            sortedGames.add(games.get(i));
        }

        return sortedGames;
    }

    //Check if selected game is yours
    public boolean checkMyGame(){
        if(game != null) {
            return game.owner().equals(tokenStorage.getUserId());
        }
        return false;
    }

    //Set a token for testing BrowseGameController
    public void setTokenStorage(){
        tokenStorage = new TokenStorage();
        tokenStorage.setName(null);
        tokenStorage.setToken(null);
        tokenStorage.setAvatar(null);
        tokenStorage.setUserId("testID");
    }
    //Calls Api DELETE if the game is from the user
    public Observable<Game> deleteGame() {
        if (checkMyGame()) {
            return gamesApiService.deleteGame(game._id());
        } else {
            return null;
        }

    }

    public String getGameName() {
        if (checkMyGame()) {
            return this.game.name();
        } else {
            return "";
        }
    }

    public boolean getBlurStatusRemoved() {
        return blurStatus = false;
    }
}
