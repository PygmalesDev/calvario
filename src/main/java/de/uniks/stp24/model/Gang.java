package de.uniks.stp24.model;
import com.fasterxml.jackson.annotation.JsonIgnore;


import javafx.scene.image.Image;

public record Gang(
        String name,
        int flagIndex,
        int portraitIndex,
        String description,
        String color,
        int colorIndex) {
}

