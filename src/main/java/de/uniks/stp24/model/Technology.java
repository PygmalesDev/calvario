package de.uniks.stp24.model;

public record Technology(
    String id,
    Effect[] effects,
    String tags,
    int cost
) {
}