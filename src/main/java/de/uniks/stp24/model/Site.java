package de.uniks.stp24.model;

public record Site(
    String siteID,
    Resource[] required,
    int maxBuildings,
    Building[] buildings
) {
}
