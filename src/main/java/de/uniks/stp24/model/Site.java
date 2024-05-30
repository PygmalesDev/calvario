package de.uniks.stp24.model;

import java.util.Map;

public record Site(
    String siteID,
    Map<Resource, Integer> required,
    int maxBuildings,
    Building[] buildings
) {
}
