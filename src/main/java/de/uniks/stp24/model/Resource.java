package de.uniks.stp24.model;


public record Resource(
    String resourceID,
    int count,
    int changePerSeason,
    ResourceType type
) {

    public enum ResourceType{
        ECONOMY, POPULATION, PRODUCTION, TACTICS
    }
}
