package de.uniks.stp24.dto;

public record UpdateGameResultDto(
    String _id,
    String name,
    String owner,
    int size
) {
}
