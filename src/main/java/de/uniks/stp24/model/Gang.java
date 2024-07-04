package de.uniks.stp24.model;
import com.fasterxml.jackson.annotation.JsonIgnore;


import javafx.scene.image.Image;

import java.util.ArrayList;

public record Gang(
        String name,
        int flagIndex,
        String flagsPath,
        int portraitIndex,
        String portraitsPath,
        String description,
        String color,
        int colorIndex,
        ArrayList<Trait> traits) {
}

