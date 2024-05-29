package de.uniks.stp24.model;

import javafx.scene.image.Image;

public record Site(
    Image icon,
    String name,
    String Description,
    Resource[] required,
    int maxBuildings,
    Building[] buildings
) {
}
