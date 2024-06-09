package de.uniks.stp24.model;

import java.util.Map;

public record UpgradeStatus(
        String id,
        int pop_growth,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        int capacity_multiplier
) {
}
