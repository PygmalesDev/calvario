package de.uniks.stp24.model;

public record LoginResult(
        String _id,
        String name,
        String avatar,
        String accessToken,
        String refreshToken
) {
}
