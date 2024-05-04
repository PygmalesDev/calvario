package de.uniks.stp24.dto;

public record CreateGameDto(
        String name,
        boolean started,
        int speed,
        int size,
        String password
) {
}
