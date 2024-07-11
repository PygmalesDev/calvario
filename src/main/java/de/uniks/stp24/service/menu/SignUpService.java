package de.uniks.stp24.service.menu;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.CreateUserDto;
import de.uniks.stp24.dto.SignUpResultDto;
import de.uniks.stp24.rest.UserApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class SignUpService {
    @Inject
    UserApiService userApiService;
    @Inject
    App app;

    @Inject
    public  SignUpService(){
    }

    // Registers a new user account on server if such a username does not exist
    public Observable<SignUpResultDto> register(String username, String password) {
        return this.userApiService.signup(new CreateUserDto(
                username, this.generateRandomAvatar(), password));
    }


    // Creates a random avatar for new user account
    private String generateRandomAvatar() {
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
    }
}
