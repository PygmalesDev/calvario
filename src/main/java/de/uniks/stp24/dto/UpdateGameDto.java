package de.uniks.stp24.dto;

public record UpdateGameDto(
        String name,
        boolean started,
        int speed,
        int size,
        String password
) {

}
