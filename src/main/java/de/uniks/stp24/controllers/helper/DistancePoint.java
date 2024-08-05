package de.uniks.stp24.controllers.helper;

import javafx.geometry.Point2D;

import java.util.Objects;

public class DistancePoint extends Point2D {
    private DistancePoint prev;

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

    public DistancePoint getPrev() {
        return prev;
    }

    public void setPrev(DistancePoint prev) {
        this.prev = prev;
    }
}
