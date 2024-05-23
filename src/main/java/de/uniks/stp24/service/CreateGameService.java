package de.uniks.stp24.service;

import de.uniks.stp24.controllers.CreateGameController;
import de.uniks.stp24.dto.CreateGameDto;
import de.uniks.stp24.dto.CreateGameResultDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class CreateGameService {
    @Inject
    GamesApiService gamesApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ErrorService errorService;

    private boolean isNameable = true;
    private ObservableList<Game> games = FXCollections.observableArrayList();
    CreateGameController createGameController;

    private
    List<String> gameNames = new ArrayList<>();

    @Inject
    public CreateGameService() {
    }

    public void setGamesList(ObservableList<Game> games){
        this.games = games;
    }

    //Check if game with same name exits already. If not, create new game.
    public Observable<CreateGameResultDto> createGame(String name, GameSettings settings, String password) {
        for (Game game1 : games) {
            if (game1.name().equals(name)){
                isNameable = false;
                break;
            }
        }

        if (isNameable) {
            System.out.println("NAMEABLE");
            return gamesApiService
                    .createGame(new CreateGameDto(name, false, 1, settings, password))
                    .doOnError(error -> createGameController.showError(errorService.getStatus(error)));

        } else {
            // code 409 -> name exits already
            createGameController.showError(409);
            isNameable = true;
            return null;
        }
    }
    public void setCreateGameController(CreateGameController createGameController){
        this.createGameController = createGameController;
    }

    private void refreshNames(){
        gameNames.clear();
        for (Game game1 : games) {
            String tmp = game1.name();
            if (gameNames.contains(tmp)) {
                gameNames.add(game1.name());
            }
        }
    }
    public boolean isCreable(String name) {
        if (!gameNames.contains(name)) {
            gameNames.add(name);
        return true;}
        for (String game1 : gameNames) {
            if (game1.equals(name)){
                System.out.println("FUCK!");
                return false;
            }
        }
        System.out.println("NO FUCK");
        return true;
    }

}
