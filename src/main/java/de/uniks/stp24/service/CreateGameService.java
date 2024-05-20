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

}
