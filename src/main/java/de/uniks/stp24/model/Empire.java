package de.uniks.stp24.model;

public record Empire(
        String name,
        String description,
        String color,
        int flag,
        int portrait,
        String[] traits,
        String homeSystem

) {
}
