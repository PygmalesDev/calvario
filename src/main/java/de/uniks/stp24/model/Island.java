package de.uniks.stp24.model;

public record Island(
  String owner,
  String avatar,
  int posX,
  int posY,
  IslandType type,
  int crewCapacity,
  int resourceCapacity,
  int upgradeLevel,
  Site[] sites
) {
}
