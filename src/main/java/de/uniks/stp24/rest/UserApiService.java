package de.uniks.stp24.rest;

import de.uniks.stp24.dto.CreateUserDto;
import de.uniks.stp24.dto.SignUpResultDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {
    @POST("users")
    Observable<SignUpResultDto> signup(@Body CreateUserDto dto);

    @DELETE("users/{id}")
    Observable<SignUpResultDto> delete(@Path("id") long id);
}
