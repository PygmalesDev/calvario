package de.uniks.stp24.model;

public record User(
        String name,
        String _id,
        String avatar,
        String createdAt,
        String updatedAt
) {
}