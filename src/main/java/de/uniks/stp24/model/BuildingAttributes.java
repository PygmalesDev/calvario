package de.uniks.stp24.model;

import java.util.Map;

public record BuildingAttributes(
        String id,
        double build_time,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        Map<String, Integer> production
) {
}
