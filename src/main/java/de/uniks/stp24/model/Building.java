package de.uniks.stp24.model;

import java.util.Map;

public record Building(
  String buildingID,
  Map<Resource, Integer> required,
  Map<Resource, Integer> production,
  Map<Resource, Integer> consumption,
  int capacity,
  int upgrade
) {
}
