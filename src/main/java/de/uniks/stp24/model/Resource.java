package de.uniks.stp24.model;


public record Resource(
    String resourceID,
    int count,
    int changePerSeason
) {
}
