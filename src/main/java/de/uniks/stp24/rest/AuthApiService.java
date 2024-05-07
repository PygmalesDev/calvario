package de.uniks.stp24.rest;

import de.uniks.stp24.dto.LoginDto;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.dto.RefreshDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    @POST("auth/login")
    Observable<LoginResult> login(@Body LoginDto loginDto);

    @POST("auth/refresh")
    Observable<LoginResult> refresh(@Body RefreshDto refreshDto);

}
