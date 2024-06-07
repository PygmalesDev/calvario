package de.uniks.stp24.dto;

import de.uniks.stp24.model.Empire;

public record MemberDto(
        boolean ready,
        String user,
        String game,
        Empire empire,
        String password
) {
}
