package de.uniks.stp24.model;

import java.util.ArrayList;
import java.util.Map;

public record Island(
  String owner,
  int flagIndex,
  double posX,
  double posY,
  IslandType type,
  int crewCapacity,
  int resourceCapacity,
  int upgradeLevel,
  Map<String, Integer> sitesSlots,
  Map<String, Integer> sites,
  ArrayList<String> buildings,
  String id,
  String upgrade,
  String name,
  int health
) {
}
