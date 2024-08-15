package de.uniks.stp24.utils;

import java.util.List;

import static java.lang.Math.*;

// There is no god but calculus...
/**
 * This class contains an object representation of a 2D-Vector with some helpful methods. <p>
 * Is used sorely to calculate the
 */
public class VectorMath {
    public static class Vector2D {
        private double x = 0;
        private double y = 0;


        public Vector2D(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Vector2D(Vector2D v) {
            this.x = v.x;
            this.y = v.y;
        }

        public Vector2D() {}

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }


        public double y() {
            return y;
        }

        public double x() {
            return x;
        }

        public double length() {
            return sqrt(pow(this.x, 2) + pow(this.y, 2));
        }

        public Vector2D normalized() {
            return new Vector2D(this.x / length(), this.y / length());
        }

        public Vector2D add(Vector2D other) {
            this.x += other.x;
            this.y += other.y;

            return this;
        }

        public Vector2D add(double x, double y) {
            this.x += x;
            this.y += y;

            return this;
        }

        public Vector2D sub(Vector2D other) {
            this.x -= other.x;
            this.y -= other.y;

            return this;
        }

        public Vector2D sub(double x, double y) {
            this.x -= x;
            this.y -= y;

            return this;
        }

        public Vector2D scale(double scalar) {
            this.x *= scalar;
            this.y *= scalar;

            return this;
        }

        public Double[] toDoubleArray() {
            return new Double[]{this.x, this.y};
        }

        @Override
        public String toString() {
            return String.format("Vector2D[x=%.2f, y=%.2f]", this.x, this.y);
        }
    }

    public static Vector2D getCentroid(List<Vector2D> vectors) {
        Vector2D centroid = new Vector2D();
        vectors.forEach(centroid::add);

        centroid.setX(centroid.x()/vectors.size());
        centroid.setY(centroid.y()/vectors.size());
        System.out.println(centroid);
        return centroid;
    }
}
