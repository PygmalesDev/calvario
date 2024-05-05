package de.uniks.stp24.rest;

import de.uniks.stp24.model.User;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;

public interface UserApiService {
    @GET("users")
    Observable<User[]> findAll();
}
