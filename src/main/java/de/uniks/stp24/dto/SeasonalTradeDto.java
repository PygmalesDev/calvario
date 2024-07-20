package de.uniks.stp24.dto;

import de.uniks.stp24.model.SeasonComponent;

import java.util.List;
import java.util.Map;

public record SeasonalTradeDto(
        Map<String,List<SeasonComponent>> _private
) {
}
