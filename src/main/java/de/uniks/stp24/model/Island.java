package de.uniks.stp24.model;

import de.uniks.stp24.dto.Upgrade;

import java.util.ArrayList;
import java.util.Map;

public record Island(
  Upgrade upgrade,
  String[] buildings,
  Map<String, Integer> districts,
  String name,
  String id_,
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
