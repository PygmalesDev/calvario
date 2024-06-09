package de.uniks.stp24.model;

public record EmpireExtendedDto(
  String createdAt,
  String updatedAt,
  String _id,
  String game,
  String user,
  String name,
  String color,
  int flag,
  int portrait,
  String[] traits,
  String homeSystem

) {
}
