package de.uniks.stp24.model;

import javafx.scene.paint.Color;

public record Gang(
        String name,
        String flag,
        String portrait,
        Color color
) {
}

