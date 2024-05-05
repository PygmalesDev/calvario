package de.uniks.stp24.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenStorage {
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NjM2NDJkYzQ2ODJkZGJkMGQzOTQ4NmYiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYWdudXMiLCJpYXQiOjE3MTQ5MDQ0MDMsImV4cCI6MTcxNDkwODAwM30.6cYdSFerPgbE4po0AT8jEq5BtL7Ct7mbpZQXPt5X-Ug";
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
