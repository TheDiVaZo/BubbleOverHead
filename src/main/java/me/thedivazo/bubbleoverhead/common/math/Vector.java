package me.thedivazo.bubbleoverhead.common.math;

import com.google.common.primitives.Doubles;
import org.bukkit.util.NumberConversions;

import java.io.Serial;
import java.io.Serializable;

public record Vector(
        double x,
        double y,
        double z
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    private static final double epsilon = 1.0E-6;

    public static double getEpsilon() {
        return 1.0E-6;
    }

    public Vector(int x, int y, int z) {
        this((double) x,(double) y,(double) z);
    }

    public Vector(float x, float y, float z) {
        this((double) x,(double) y,(double) z);
    }

    public Vector x(double x) {
        return new Vector(x, y, z);
    }

    public Vector y(double y) {
        return new Vector(x, y, z);
    }

    public Vector z(double z) {
        return new Vector(x, y, z);
    }

    public Vector add(Vector v) {
        return new Vector(x() + v.x(), y() + v.y(), z() + v.z());
    }

    public Vector subtract(Vector v) {
        return new Vector(x() - v.x(), y() - v.y(), z() - v.z());
    }

    public Vector multiply(Vector v) {
        return new Vector(x() * v.x(), y() * v.y(), z() * v.z());
    }

    public Vector divide(Vector v) {
        return new Vector(x() / v.x(), y() / v.y(), z() / v.z());
    }

    public double length() {
        return Math.sqrt(NumberConversions.square(x()) + NumberConversions.square(y()) * NumberConversions.square(z()));
    }

    public double lengthSquared() {
        return NumberConversions.square(x()) + NumberConversions.square(y()) * NumberConversions.square(z());
    }

    public double distance(Vector v) {
        return Math.sqrt(NumberConversions.square(this.x - v.x) + NumberConversions.square(this.y - v.y) + NumberConversions.square(this.z - v.z));
    }

    public double distanceSquared(Vector o) {
        return NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z);
    }

    public float angle(Vector other) {
        double dot = Doubles.constrainToRange(this.dot(other) / (this.length() * other.length()), (double)-1.0F, (double)1.0F);
        return (float)Math.acos(dot);
    }

    public Vector midpoint(Vector other) {
        return new Vector(
                (this.x + other.x) / (double)2.0F,
                (this.y + other.y) / (double)2.0F,
                (this.z + other.z) / (double)2.0F
        );
    }

    public Vector getMidpoint(Vector other) {
        double x = (this.x + other.x) / (double)2.0F;
        double y = (this.y + other.y) / (double)2.0F;
        double z = (this.z + other.z) / (double)2.0F;
        return new Vector(x, y, z);
    }

    public Vector multiply(int m) {
        return new Vector(x() * m, y() * m, z() * m);
    }

    public Vector multiply(double m) {
        return new Vector(this.x * m, this.y * m, this.z * m);
    }

    public Vector multiply(float m) {
        return new Vector(this.x * m, this.y * m, this.z * m);
    }

    public double dot(Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public Vector normalize() {
        double length = this.length();
        return new Vector(this.x / length, this.y / length, this.z / length);
    }

    public boolean isInAABB(Vector min, Vector max) {
        return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z;
    }

    public boolean isInSphere(Vector origin, double radius) {
        return NumberConversions.square(origin.x - this.x) + NumberConversions.square(origin.y - this.y) + NumberConversions.square(origin.z - this.z) <= NumberConversions.square(radius);
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - (double)1.0F) < getEpsilon();
    }

    public boolean equals(Object obj) {
        if (obj instanceof Vector other) {
            return Math.abs(this.x - other.x) < 1.0E-6 && Math.abs(this.y - other.y) < 1.0E-6 && Math.abs(this.z - other.z) < 1.0E-6;
        }
        return false;
    }

    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 79 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        return hash;
    }
}
