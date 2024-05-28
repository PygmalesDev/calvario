package de.uniks.stp24.service;

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
    // list with avatar is required
    private String generateRandomAvatar() {
        String [] avatars = {"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAAsSAAALEgHS3X78AAABWUlEQVRYhe2XoU7EQBCGvxIeAnECA54E2SIreIxa9JmSIEjoY9TjCaK2lQiwEJITJy4nCb6I3rTpXOf2CGKX5H7TbGeb3fk6OzMbtW2LTx15XR04fnz43jmhKOM/IcqzJtpl90+gru4nDfWyagGSWQrA2enlyH5yfr3XAkIwmaWTJPwT0C+050l6B8Dq/Xk0T48t5VkD2CS8E4hurubAtuci/e+1dCxYZGReUcZs1okgAAJbMSASzy0Py6boXmyeVsxMeA4MxL0TOGzgsAHzFHwsXoAhivfNfDr6Xd+FS0BkeZDFOWBXRTn3UgsyZa+XFRAAgb4WiKyaYFVFrT5DyneqnyiHzBloLZCdCYle3S9zVkeJDa0+VhSh8AiIdOeiiQgJyReusaVwCWhZsSGnQ7pr11jr/xCw5OqWZSyZL7yu2HU7vr3oMtnT11qbfntnHHn+9vkKBEDgB38ml0DtoR0UAAAAAElFTkSuQmCC",
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABR0lEQVRYhWP8//8/w0ACpgG1nYGBgbG+oAuvgvWbllIURIF+0Yz45Ac8BFhwScB8HugXTZRBj5/eRuHfefAQ3RysITH4QgDd5+g+IxY4WrswMDAwMOw/ugfdXJSQGDwhQKzPZaVVUfi41KGLCwiLo9vDyMAwGEIA5iIkFzIwMGCGBMznsDj98PYlikEmxmYo6tABLFeggwEPgVEHjDoAZ12AnvqJLRHR9eFK/TAweEJARUEewoDSMB+glwvkloS4wICHALxFBCsRYSUaLO5wlXi4AHqJCQMwcwZvbQgDGGU5LG0QCXDVFUOnRQRLvbA0AMsdxPJxxTUuMPhCAOYT9DYdIT6pPoeBgQ8BWJybGJsxMjAwMJw5e+o/AwNmSUaIT0gcFxjwEGCE9Y71lQ3Q5UjtE6LEvQ+fGAMDAwND6/ldeDUNeAgAAE7prIZuohotAAAAAElFTkSuQmCCiVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg=="};
        return avatars[0];
    }
}
