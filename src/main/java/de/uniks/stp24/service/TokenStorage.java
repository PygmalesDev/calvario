package de.uniks.stp24.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenStorage {
    private String token;
    private String userId;
    private String name;
    private String avatar;
    private String gameId;
    private String empireId;

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

    public String getGameId() {return gameId;}
    public void setGameId(String gameId) {this.gameId = gameId;}

    public String getEmpireId() {return empireId;}
    public void setEmpireId(String empireId) {this.empireId = empireId;}
}
