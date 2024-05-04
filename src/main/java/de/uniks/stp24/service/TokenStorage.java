package de.uniks.stp24.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TokenStorage {
    private String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2NjM2NDJkYzQ2ODJkZGJkMGQzOTQ4NmYiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYWdudXMiLCJpYXQiOjE3MTQ4MzY4MjAsImV4cCI6MTcxNDg0MDQyMH0.RcrsCoeRnxu7Yyez7KYPOkk5pYlhx4-pgwkMTNbmHYg";
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
