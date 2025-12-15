package me.thedivazo.bubbleoverhead.common.math;


import me.thedivazo.bubbleoverhead.common.World;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public record Location(
        double x,
        double y,
        double z,
        float pitch,
        float yaw,
        World world
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1;

    public Location x(double x) {
        return new Location(x, y, z, pitch, yaw, world);
    }

    public Location y(double y) {
        return new Location(x, y, z, pitch, yaw, world);
    }

    public Location z(double z) {
        return new Location(x, y, z, pitch, yaw, world);
    }

    public Location pitch(float pitch) {
        return new Location(x, y, z, pitch, yaw, world);
    }

    public Location yaw(float yaw) {
        return new Location(x, y, z, pitch, yaw, world);
    }

    public Location world(World world) {
        return new Location(x, y, z, pitch, yaw, world);
    }

    public Location add(Vector vector) {
        return new Location(x() + vector.x(), y() + vector.y(), z() + vector.z(), pitch, yaw, world);
    }

    public Location subtract(Vector vector) {
        return new Location(x() - vector.x(), y() - vector.y(), z() - vector.z(), pitch, yaw, world);
    }

    public Vector getDirection() {
        double rotX = this.yaw();
        double rotY = this.pitch();
        double xz = Math.cos(Math.toRadians(rotY));

        double y = -Math.sin(Math.toRadians(rotY));
        double x = -xz * Math.sin(Math.toRadians(rotX));
        double z = xz * Math.cos(Math.toRadians(rotX));

        return new Vector(x, y, z);
    }

    public Location setDirection(Vector vector) {
        double x = vector.x();
        double z = vector.z();
        float pitch;
        float yaw = this.yaw();
        if (x == (double)0.0F && z == (double)0.0F) {
            pitch = (float)(vector.y() > (double)0.0F ? -90 : 90);
        } else {
            double theta = Math.atan2(-x, z);
            yaw = (float)Math.toDegrees((theta + (Math.PI * 2D)) % (Math.PI * 2D));
            double x2 = x*x;
            double z2 = z*z;
            double xz = Math.sqrt(x2 + z2);
            pitch = (float)Math.toDegrees(Math.atan(-vector.y() / xz));
        }
        return new Location(x, this.y, z, pitch, yaw, world);
    }

    boolean inAnotherWorld(Location position) {
        return !Objects.equals(this.world().uuid(), position.world().uuid());
    }

    static double distance3D(Location loc1, Location loc2) {
        if (loc1.inAnotherWorld(loc2)) return Double.POSITIVE_INFINITY;
        double dx = loc1.x() - loc2.x();
        double dy = loc1.y() - loc2.y();
        double dz = loc1.z() - loc2.z();
        return Math.hypot(Math.hypot(dx, dy), dz);
    }

    static double distance2D(Location loc1, Location loc2) {
        if (loc1.inAnotherWorld(loc2)) return Double.POSITIVE_INFINITY;
        double dx = loc1.x() - loc2.x();
        double dz = loc1.z() - loc2.z();
        return Math.hypot(dx, dz);
    }

    double distance3D(Location loc) {
        return distance3D(this, loc);
    }

    double distance2D(Location loc) {
        return distance2D(this, loc);
    }

    Vector toVector() {
        return new Vector(x, y, z);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Location other = (Location)obj;
            World world = this.world;
            World otherWorld = other.world;
            if (Objects.equals(world, otherWorld)) {
                if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
                    return false;
                } else if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
                    return false;
                } else if (Double.doubleToLongBits(this.z) != Double.doubleToLongBits(other.z)) {
                    return false;
                } else if (Float.floatToIntBits(this.pitch) != Float.floatToIntBits(other.pitch)) {
                    return false;
                } else {
                    return Float.floatToIntBits(this.yaw) == Float.floatToIntBits(other.yaw);
                }
            } else {
                return false;
            }
        }
    }

    public int hashCode() {
        int hash = 3;
        World world = this.world;
        hash = 19 * hash + (world != null ? world.hashCode() : 0);
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.x));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.y));
        hash = 19 * hash + Long.hashCode(Double.doubleToLongBits(this.z));
        hash = 19 * hash + Float.floatToIntBits(this.pitch);
        hash = 19 * hash + Float.floatToIntBits(this.yaw);
        return hash;
    }
}
