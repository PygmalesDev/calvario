package de.uniks.stp24.dto;

public record CreateUserDto(
        String name,
        String avatar,
        String password) {
}
