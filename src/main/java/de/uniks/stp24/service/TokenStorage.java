package de.uniks.stp24.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenStorage {
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NjM2NDJkYzQ2ODJkZGJkMGQzOTQ4NmYiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYWdudXMiLCJpYXQiOjE3MTQ5MDA1OTksImV4cCI6MTcxNDkwNDE5OX0.Ms5wP0JAZLtTLfEu5DfQVALyIh0tEVY0s9SuCEq6dII";
    private String userId;

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
}
