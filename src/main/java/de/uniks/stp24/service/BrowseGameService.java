package de.uniks.stp24.service;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BrowseGameService {
    @Inject
    GamesApiService GamesApiService;

    @Inject
    TokenStorage tokenStorage;
    @Inject
    PrefService prefService;

    private Game game;

    @Inject
    public BrowseGameService() {
    }

    public void setGame(Game game){
        this.game = game;
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
}
