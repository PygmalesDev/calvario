package de.uniks.stp24.model;

public record TechnologyExtended(
        String id,
        Effect[] effects,
        String[] tags,
        int cost,
        String[] requires,
        String[] precedes
) {
}
