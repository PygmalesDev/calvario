package de.uniks.stp24.service;

import dagger.Provides;
import de.uniks.stp24.dto.UpdateGameDto;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EditGameService {
    @Inject
    GamesApiService gamesApiService;

    @Inject
    TokenStorage tokenStorage;

    Game game;
    @Inject
    public EditGameService() {
    }

    public void setClickedGame(Game game){
        this.game = game;
    }

    public Observable<UpdateGameResultDto> editGame(String name, GameSettings settings, String password){
        System.out.println("#########" + this.game._id());
        return gamesApiService.editGame(this.game._id(), new UpdateGameDto(name,false,1, settings, password));
    }
}
