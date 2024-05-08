package de.uniks.stp24.service;

import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.Empire;
import de.uniks.stp24.rest.GameMembersApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class JoinGameService {
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public JoinGameService() {
    }
    @Inject
    GameMembersApiService gameMembersApiService;

    public Observable<JoinGameDto> joinGame(String gameID, String password) {
        return gameMembersApiService.joinGame(gameID, this.loadUserLobbyDto(password));
    }


    private MemberDto loadUserLobbyDto(String password) {
        return new MemberDto(
            false,
                this.tokenStorage.getUserId(),
                this.loadBlankEmpire(),
                password
        );
    }
    private Empire loadBlankEmpire() {
        return new Empire(
          "unnamed",
          "description",
          "#9ed5ff", 32, 32,
                "uninhabitable_0",
                new String[]{"weak"}
        );
    }
}
