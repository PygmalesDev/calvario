package de.uniks.stp24.dto;

import java.util.Map;

public record UpdateEmpireDto(
        Map<String, Integer> resources,
        String[] technologies
) {
}
