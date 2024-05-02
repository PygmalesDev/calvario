package de.uniks.stp24.service;

import de.uniks.stp24.controllers.SignUpController;
import de.uniks.stp24.dto.CreateUserDto;
import de.uniks.stp24.rest.UserApiService;

import javax.inject.Inject;
import java.util.Map;

public class SignUpService {
    @Inject
    SignUpController signUpController;
    @Inject
    UserApiService userApiService;

    // Registers a new user account on server if such username does not exist
    public boolean register(String username, String password) {
        if (this.checkForAccountExistence(username))
                return false;
        this.userApiService.signup(new CreateUserDto(username, password)).subscribe(
                user -> Map.of(
                        "username", username,
                        "password", password
                ));
        return true;
    }

    // Checks for the existence of an account with the same username
    // TODO: Implement!
    private boolean checkForAccountExistence(String username) {
        return true;
    }
}
