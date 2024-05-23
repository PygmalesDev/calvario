package de.uniks.stp24.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.uniks.stp24.service.ColorDeserializer;
import javafx.scene.paint.Color;

public record Gang(
        String name,
        String flag,
        int flagIndex,
        String portrait,
        int portraitIndex,
        String description,
        String color,
        int colorIndex) {
}

