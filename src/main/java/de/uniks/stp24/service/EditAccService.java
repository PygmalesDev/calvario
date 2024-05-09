package de.uniks.stp24.service;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.UpdateUserDto;
import de.uniks.stp24.model.User;
import de.uniks.stp24.rest.UserApiService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class EditAccService {
    @Inject
    UserApiService userApiService;
    @Inject
    App app;

    //private User user;
    public User user = new User("a","b","c","d","e");


    @Inject
    public EditAccService() {}

    public Observable<User> changeUserInfo(String newUsername, String newPassword){
        return userApiService.edit(Long.parseLong(user._id()), new UpdateUserDto(newUsername,user.avatar(), newPassword));
    }

    public Observable<User> deleteUser(){
        return userApiService.delete(Long.parseLong(user._id()));
    }

}
