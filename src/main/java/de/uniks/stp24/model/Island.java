package de.uniks.stp24.model;

import java.util.ArrayList;
import java.util.Map;

public record Island(
        String createdAt,
        String updatedAt,
        String _id,
        String game,
        IslandType type,
        String name,
        Map<String, Integer> districtSlots,
        Map<String, Integer> districts,
        int capacity,
        ArrayList<String> buildings,
        String upgrade,
        int population,
        Map<String, Integer> links,
        int x,
        int y,
        String owner
) {
}
