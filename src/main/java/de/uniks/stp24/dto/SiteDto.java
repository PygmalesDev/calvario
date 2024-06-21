package de.uniks.stp24.dto;

import java.util.Map;

public record SiteDto(
        String id,
        Map<String, Integer> chance,
        Map<String, Integer> cost,
        Map<String, Integer> upkeep,
        Map<String, Integer> production

        ) {
}
