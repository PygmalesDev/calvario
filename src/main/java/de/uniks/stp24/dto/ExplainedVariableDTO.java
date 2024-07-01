package de.uniks.stp24.dto;

import de.uniks.stp24.model.Sources;

import java.util.ArrayList;

public record ExplainedVariableDTO(
        String variable,
        double initial,
        ArrayList<Sources> sources,
        double finalValue
) {
}
