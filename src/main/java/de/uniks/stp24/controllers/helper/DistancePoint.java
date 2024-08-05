package de.uniks.stp24.controllers.helper;

import javafx.geometry.Point2D;

import java.util.Objects;

public class DistancePoint extends Point2D {
    private final Point2D prev;

    public DistancePoint(double v, double v1, DistancePoint prev) {
        super(v, v1);
        this.prev = prev;
    }

    public DistancePoint(DistancePoint other) {
        super(other.getX(), other.getY());
        this.prev = other.prev;
    }

    public double getDistance() {
        return Objects.nonNull(prev) ? this.distance(prev) : -1;
    }

    public Point2D getPrev() {
        return prev;
    }
}
