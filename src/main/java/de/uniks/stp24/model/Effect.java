package de.uniks.stp24.model;

public record Effect(
        String variable,
        int base,
        int multiplier,
        int bonus
) {
}
