package de.uniks.stp24.service;

import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.rest.GameMembersApiService;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

public class LobbyService {
    @Inject
    GameMembersApiService gameMembersApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    UserApiService userApiService;
    @Inject
    EventListener eventListener;

    @Inject
    TokenStorage tokenStorage;

    @Inject
    public LobbyService() {}
    public Observable<MemberDto[]> loadPlayers(String gameID) {
        return this.gameMembersApiService.getMembers(gameID);
    }

    public Observable<JoinGameDto> leaveLobby(String gameID) {
        return this.gameMembersApiService.leaveGame(gameID, this.tokenStorage.getUserId());
    }
}
