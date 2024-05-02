package de.uniks.stp24.service;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.SignUpController;
import de.uniks.stp24.dto.CreateUserDto;
import de.uniks.stp24.rest.UserApiService;

import javax.inject.Inject;
import java.util.Map;

public class SignUpService {
    @Inject
    UserApiService userApiService;

    @Inject
    App app;

    @Inject
    public  SignUpService(){
    }

    // Registers a new user account on server if such username does not exist
    public boolean register(String username, String password) {
        if (!this.checkForAccountExistence(username))
                return false;
        this.userApiService.signup(new CreateUserDto(
                username, this.generateRandomAvatar(), password)
        ).subscribe(
                user -> app.show("/login", Map.of(
                        "username", username,
                        "password", password
                )));
        return true;
    }

    // Checks for the existence of an account with the same username
    // TODO: Implement!
    private boolean checkForAccountExistence(String username) {
        return true;
    }

    // Creates a random avatar for new user account
    // TODO: Implement!
    private String generateRandomAvatar() {
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
    }
}
