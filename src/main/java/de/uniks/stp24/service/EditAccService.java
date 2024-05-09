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
    @Inject
    TokenStorage tokenStorage;




    @Inject
    public EditAccService() {}

    public Observable<User> changeUserInfo(String newUsername, String newPassword){
        return userApiService
                .edit(tokenStorage.getUserId(), new UpdateUserDto(newUsername, tokenStorage.getAvatar(), newPassword))
                .doOnNext(editResult ->{
                    tokenStorage.setName(editResult.name());
                });
    }

    public Observable<User> deleteUser(){
        return userApiService
                .delete(tokenStorage.getUserId())
                .doOnNext(deleteResult ->{
                    tokenStorage.setName(null);
                    tokenStorage.setAvatar(null);
                });
    }

}
