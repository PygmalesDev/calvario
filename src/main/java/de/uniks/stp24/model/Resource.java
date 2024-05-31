package de.uniks.stp24.model;


public record Resource(
    String name,
    int count,
    int proSeason,
    ResourceType type
) {

    public enum ResourceType{
        ECONOMY, POPULATION, PRODUCTION, TACTICS
    }
}
