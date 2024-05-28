package de.uniks.stp24.model;

public record Island(
  String owner,
  String avatar,
  int latitude,
  int meridian,
  IslandType type,
  int crewCapacity,
  int resourceCapacity,
  int upgradeLevel,
  Site[] sites,

) {
}
