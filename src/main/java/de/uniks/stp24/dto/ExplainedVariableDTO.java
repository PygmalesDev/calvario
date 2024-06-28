package de.uniks.stp24.dto;

import de.uniks.stp24.model.Sources;

public record ExplainedVariableDTO(
    String variable,
    double initial,
    Sources sources,
    double finalValue
) {
}
