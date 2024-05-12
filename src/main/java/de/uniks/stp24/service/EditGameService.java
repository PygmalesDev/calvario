package de.uniks.stp24.service;

import dagger.Provides;
import de.uniks.stp24.controllers.EditGameController;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EditGameService {
    @Inject
    GamesApiService gamesApiService;
    EditGameController editGameController;

    @Inject
    TokenStorage tokenStorage;
    private ObservableList<Game> games = FXCollections.observableArrayList();
    boolean isNameable = true;


    Game game;
    @Inject
    public EditGameService() {
    }

    public void setClickedGame(Game game){
        this.game = game;
    }

    public void setGamesList(ObservableList<Game> games){
        this.games = games;
    }

    public Observable<UpdateGameResultDto> editGame(String name, GameSettings settings, String password){
        editGameController.hideErrorBox();
        for (Game game1 : games) {
            if (game1.name().equals(name)){
                isNameable = false;
                break;
            }
        }

        if (isNameable){
            return gamesApiService.editGame(this.game._id(), new UpdateGameDto(name,false,1, settings, password));
        } else {
            editGameController.showNameTakenError();
            isNameable = true;
            System.out.println("Name exists already!");
            return null;
        }

    }

    public void setEditGameController(EditGameController editGameController) {
        this.editGameController = editGameController;
    }
}
