package de.uniks.stp24.service;

import de.uniks.stp24.controllers.CreateGameController;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;
import jakarta.websocket.OnError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateGameService {
    @Inject
    GamesApiService gamesApiService;

    @Inject
    TokenStorage tokenStorage;

    private boolean isNameable = true;
    private ObservableList<Game> games = FXCollections.observableArrayList();
    CreateGameController createGameController;

    @Inject
    public CreateGameService() {
    }

    public void setGamesList(ObservableList<Game> games){
        this.games = games;
    }

    public Observable<CreateGameResultDto> createGame(String name, GameSettings settings, String password){
        createGameController.hideErrorBox();
        for (Game game1 : games) {
            if (game1.name().equals(name)){
                isNameable = false;
                break;
            }
        }

        if (isNameable) {
            return gamesApiService
                    .createGame(new CreateGameDto(name, false, 1, settings, password))
                    .doOnNext(createGameResult -> {
                        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                    })
                    .doOnError(error -> {
                        showErrorBox();
                    });
        } else {
            createGameController.showNameTakenError();
            isNameable = true;
            System.out.println("Name is already taken!");
            return null;
        }
    }
    public void setCreateGameController(CreateGameController createGameController){
        this.createGameController = createGameController;
    }
    public void showErrorBox(){
        createGameController.showErrorBox();
    }
}
