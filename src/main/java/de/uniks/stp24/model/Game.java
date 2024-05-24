package de.uniks.stp24.model;

// original was Game<x>
public record Game(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String owner,
        boolean started,
        int speed,
        int period,
        GameSettings settings
) {
}
