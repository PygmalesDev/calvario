package de.uniks.stp24.model;

import java.util.Map;

public record BuildingPresets(
        String id,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        Map<String, Integer> production
) {
}
