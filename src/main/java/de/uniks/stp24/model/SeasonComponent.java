package de.uniks.stp24.model;

public record SeasonComponent(
        String transActionTypeText,
        String resourceType,
        int resourceAmount,
        int moneyAmount,
        boolean isPlaying
) {
}
