package de.uniks.stp24.dto;

public record LoginResult(
        String _id,
        String name,
        String avatar,
        String accessToken,
        String refreshToken
) {
}
