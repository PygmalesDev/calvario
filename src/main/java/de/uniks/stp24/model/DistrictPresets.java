package de.uniks.stp24.model;

import java.util.Map;

public record DistrictPresets(
        String id,
        Map<String, Integer> chance,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        Map<String, Integer> production
) {
}
