package de.uniks.stp24.dto;

import de.uniks.stp24.model.GameSettings;

public record CreateGameDto(
        String name,
        boolean started,
        int speed,
        GameSettings settings,
        String password
) {
}

