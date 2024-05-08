package de.uniks.stp24.rest;

import de.uniks.stp24.model.User;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface UserApiService {
    @GET("users")
    Observable<User[]> findAll();

    @GET("users")
    Observable<User[]> findAll(@Query("ids") String[] ids);

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String userID);
}
