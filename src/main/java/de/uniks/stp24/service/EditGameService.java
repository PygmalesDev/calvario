package de.uniks.stp24.service;

import de.uniks.stp24.controllers.EditGameController;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
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
    @Inject
    ErrorService errorService;
    private ObservableList<Game> games = FXCollections.observableArrayList();
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

    // Editing an existing game, which is yours.
    // class was modified! some code was deleted
    // error handle occurs now in controller
    public Observable<UpdateGameResultDto> editGame(String name, GameSettings settings, String password){
            return gamesApiService
                    .editGame(this.game._id(), new UpdateGameDto(name,false,1, settings, password))
                    .doOnError(error -> editGameController.showError(errorService.getStatus(error)));
    }

    public void setEditGameController(EditGameController editGameController) {
        this.editGameController = editGameController;
    }

    public boolean nameIsAvailable(String name){
        for (Game it : games) {
            if (it.name().equals(name)){
                return false;
            }
        }
        return true;
    }
}
