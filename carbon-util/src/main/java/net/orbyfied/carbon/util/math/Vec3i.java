package net.orbyfied.carbon.util.math;

import java.util.StringJoiner;

public class Vec3i {

    int x;
    int y;
    int z;

    public Vec3i(int x,
                 int y,
                 int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3i(int[] c) {
        this.x = c[0];
        this.y = c[1];
        this.z = c[2];
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

}
