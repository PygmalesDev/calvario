package de.uniks.stp24.model;

public record Island(
  String owner,
  int flagIndex,
  double posX,
  double posY,
  IslandType type,
  int crewCapacity,
  int resourceCapacity,
  int upgradeLevel
//  Site[] sites
) {
}
