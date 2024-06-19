package de.uniks.stp24.dto;

import java.util.Map;

public record ShortSystemDto(
  String owner,
  String _id,
  String type,
  String name,
  Map<String, Integer> districtSlots,
  Map<String, Integer> districts,
  int capacity,
  String[] buildings,
  Upgrade upgrade,
  int population
) {
}