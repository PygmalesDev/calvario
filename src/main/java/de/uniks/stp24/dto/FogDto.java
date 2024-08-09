package de.uniks.stp24.dto;

import javafx.scene.shape.Shape;

import java.util.Map;

public record FogDto(
        Map<String, Shape> _private
) {
}
