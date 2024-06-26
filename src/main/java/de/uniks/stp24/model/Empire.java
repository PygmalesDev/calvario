package de.uniks.stp24.model;

import java.util.ArrayList;

public record Empire(
        String name,
        String description,
        String color,
        int flag,
        int portrait,
        ArrayList<Trait> traits,
        String homeSystem

) {
}
