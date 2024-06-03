package de.uniks.stp24.model;


import javafx.scene.image.Image;

public record GangElement(
        Gang gang,
        Image flag,
        Image portrait
) {
}
