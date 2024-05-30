package de.uniks.stp24.model;

public record Island(
  String owner,
  int flagIndex,
  int posX,
  int posY,
  IslandType type,
  int crewCapacity,
  int resourceCapacity,
  int upgradeLevel,
  Site[] sites
) {
}
