package de.uniks.stp24.model;

import java.util.Map;

public record UpgradeStatus(
        String id,
        float pop_growth,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        float capacity_multiplier
) {
}
