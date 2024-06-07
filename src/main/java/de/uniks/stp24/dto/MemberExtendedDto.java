package de.uniks.stp24.dto;

import de.uniks.stp24.model.EmpireExtendedDto;

public record MemberExtendedDto(
  boolean ready,
  String user,
  String game,
  ReadEmpireDto empire,
  String password
) {
}
