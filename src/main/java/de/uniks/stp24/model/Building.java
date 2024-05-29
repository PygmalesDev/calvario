package de.uniks.stp24.model;

import javafx.scene.image.Image;

public record Building(
  Image icon,
  String name,
  String description,
  Resource[] required,
  Resource[] production,
  Resource[] consumption,
  int capacity,
  int upgrade
) {
}
