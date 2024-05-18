package de.uniks.stp24.model;

import javax.inject.Inject;
import java.awt.*;

public record Game<x>(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String owner,

        Boolean started,

        int speed,
        int period,
        GameSettings settings
) {
}
