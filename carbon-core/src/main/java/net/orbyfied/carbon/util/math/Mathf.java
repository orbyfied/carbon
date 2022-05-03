package net.orbyfied.carbon.util.math;

public class Mathf {

    public static int in(int n, int from, int to) {
        return Math.min(to, Math.max(from, n));
    }

}
