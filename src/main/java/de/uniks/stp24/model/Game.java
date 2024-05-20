package de.uniks.stp24.model;

public record Game<x>(
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
