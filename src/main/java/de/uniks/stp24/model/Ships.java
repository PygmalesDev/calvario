package de.uniks.stp24.model;

import java.util.Map;

public class Ships {

    public record Ship(
            String createdAt,
            String updatedAt,
            String _id,
            String game,
            String empire,
            String fleet,
            String type,
            int health,
            int experience,
            Map<String, Object> _public,
            Map<String, Object> _private
    ){}

    public record ReadShipDTO(
            String createdAt,
            String updatedAt,
            String _id,
            String game,
            String empire,
            String fleet,
            String type,
            int experience,
            Map<String, Object> _public
    ) {}

    public record UpdateShipDTO(
            String fleet,
            Map<String, Object> _public,
            Map<String, Object> _private
    ) {}

    public record ShipType(
            String _id,
            int build_time,
            int health,
            int speed,
            Map<String, Integer> attack,
            Map<String, Integer> defense,
            Map<String, Integer> cost,
            Map<String, Integer> upkeep
    ){}

    public record BlueprintInFleetDto(
            String type,
            int count
    ){}
}
