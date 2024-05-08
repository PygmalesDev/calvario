package de.uniks.stp24.rest;

import de.uniks.stp24.dto.JoinGameDto;
import de.uniks.stp24.dto.MemberDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

public interface GameMembersApiService {
    @GET("games/{game}/members")
    Observable<MemberDto[]> getMembers(@Path("game") String gameID);

    @POST("games/{game}/members")
    Observable<JoinGameDto> joinGame(@Path("game") String gameID, @Body MemberDto memberDto);

    @DELETE("games/{game}/members/{user}")
    Observable<JoinGameDto> leaveGame(@Path("game") String gameID, @Path("user") String userID);
}
