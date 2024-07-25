package de.uniks.stp24.rest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.ArrayList;

import static de.uniks.stp24.model.Ships.*;

public interface ShipsApiService {
    @GET("games/{game}/fleets/{fleet}/ships")
    Observable<ArrayList<ReadShipDTO>> getAllShips(@Path("game") String gameID, @Path("fleet") String fleetID);

    @GET("games/{game}/fleets/{fleet}/ships/{id}")
    Observable<Ship> getYourOwnShip(@Path("game") String gameID, @Path("fleet") String fleetID, @Path("id") String shipID);

    @GET("games/{game}/fleets/{fleet}/ships/{id}")
    Observable<Ship> getOtherPlayersShip(@Path("game") String gameID, @Path("fleet") String fleetID, @Path("id") String shipID);

    @PATCH("games/{game}/fleets/{fleet}/ships/{id}")
    Observable<Ship> patchShip(@Path("game") String gameID, @Path("fleet") String fleetID, @Path("id") String shipID);

    @DELETE("games/{game}/fleets/{fleet}/ships/{id}")
    Observable<Ship> deleteShip(@Path("game") String gameID, @Path("fleet") String fleetID, @Path("id") String shipID);

}

