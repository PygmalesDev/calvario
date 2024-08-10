package de.uniks.stp24.controllers.helper;

import de.uniks.stp24.component.game.IslandComponent;
import javafx.geometry.Point2D;

import static de.uniks.stp24.service.Constants.*;

public class DistancePoint extends Point2D {
    private final DistancePoint prev;
    private final POINT_TYPE pointType;

    public DistancePoint(double v, double v1, POINT_TYPE type, DistancePoint prev) {
        super(v, v1);
        this.prev = prev;
        this.pointType = type;
    }

    public DistancePoint(IslandComponent island, DistancePoint prev) {
        super(island.getLayoutX() + FLEET_HW, island.getLayoutY() + FLEET_HW);
        this.prev = prev;
        this.pointType = POINT_TYPE.ISLAND;
    }

    public DistancePoint getPrev() {
        return prev;
    }

    public POINT_TYPE getType() {
        return pointType;
    }
}
