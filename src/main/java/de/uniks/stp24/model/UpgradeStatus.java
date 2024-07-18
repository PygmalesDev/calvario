package de.uniks.stp24.model;

import java.util.Map;

public record UpgradeStatus(
        String id,
        String next,
        double upgrade_time,
        double pop_growth,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        double capacity_multiplier
) {
}
