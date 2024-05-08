package de.uniks.stp24.model;

public record Game(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String owner,
        Boolean started,
        int speed,
        int period,
        GameSettings settings
) {
}
