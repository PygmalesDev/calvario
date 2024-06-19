package de.uniks.stp24.dto;

public record AggregateItemDto(
        String variable,
        int count,
        int subtotal
) {
}
