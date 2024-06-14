package de.uniks.stp24.service;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class TokenStorage {
    private String token;
    private String userId;
    private String name;
    private String avatar;
    private Map<String,Integer> avatarMap;

    @Inject
    public TokenStorage() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getAvatar() {return avatar;}

    public void setAvatar(String avatar) {this.avatar = avatar;}

    public void setAvatarMap(Map<String,Integer> avatarMap) {this.avatarMap = avatarMap;}

    public Map<String,Integer> getAvatarMap() {
        return this.avatarMap;
    }
}
