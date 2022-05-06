package net.orbyfied.carbon.api.util;

import net.orbyfied.carbon.command.CommandEngine;
import net.orbyfied.carbon.command.impl.SystemParameterType;
import net.orbyfied.carbon.command.minecraft.MinecraftParameterType;
import org.bukkit.entity.Player;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**
 * Version class specifying a format
 * as {@code major.minor.patch}.
 * Patch is a float allowing for
 * additional precision if needed.
 */
public class Version {

    public static class MalformedVersionException extends RuntimeException {

        public MalformedVersionException(String s) {
            super(s);
        }

    }

    public static Version of(String s) {
        // parse string into components
        StringBuilder b = new StringBuilder();
        String[] components = new String[3];
        int ci = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '.' && ci != 2) {
                components[ci++] = b.toString();
            } else {
                b.append(c);
            }
        }

        if (ci == 2)
            components[2] = b.toString();
        else if (ci < 2)
            throw new MalformedVersionException("for input string: " + s);

        // parse components
        try {
            int major = Integer.parseInt(components[0]);
            int minor = Integer.parseInt(components[1]);
            float patch = 0f;
            if (components[2] != null)
                patch = Float.parseFloat(components[2]);
            return new Version(major, minor, patch);
        } catch (NumberFormatException | NullPointerException e) {
            throw new MalformedVersionException("for input string: " + s);
        }
    }

    public static Version readFromResource(Class<?> ref, String path) {
        try {
            if (!path.startsWith("/"))
                path = "/" + path;
            InputStream is = ref.getResourceAsStream(path);
            Version ver = of(new String(is.readAllBytes(), StandardCharsets.UTF_8));
            is.close();
            return ver;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String patchToString(float patch) {
        if (patch % 1 == 0)
            return Integer.toString((int)patch);
        return Float.toString(patch);
    }

    public final static BiFunction<Integer, Integer, Integer> MAJORS_GREATER =
            (a, b) -> {
                if (a > b) return 1;
                else if (a < b) return -1;
                return 0;
            };

    public final static BiFunction<Integer, Integer, Integer> MAJORS_LESS =
            (a, b) -> {
                if (a < b) return 1;
                else if (a > b) return -1;
                return 0;
            };

    public final static BiFunction<Integer, Integer, Integer> MAJORS_EQUAL =
            (a, b) -> a.equals(b) ? 1 : -1;

    ////////////////////

    private int   major;
    private int   minor;
    private float patch;

    public Version(
            int major,
            int minor,
            float patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public float getPatch() {
        return patch;
    }

    public boolean compareStatement(Version with,
                                    BiFunction<Integer, Integer, Integer> aComparator,
                                    BiPredicate<Float, Float> patchComparator) {
        int c;

        c = aComparator.apply(major, with.major);
        if (c == 1) return true;
        else if (c == -1) return false;
        c = aComparator.apply(minor, with.minor);
        if (c == 1) return true;
        else if (c == -1) return false;

        return patchComparator.test(patch, with.patch);
    }

    public boolean isGreater(Version v) {
        return compareStatement(v, MAJORS_GREATER, (a, b) -> a > b);
    }

    public boolean isGreaterOrEqual(Version v) {
        return compareStatement(v, MAJORS_GREATER, (a, b) -> a >= b);
    }

    public boolean isLower(Version v) {
        return compareStatement(v, MAJORS_LESS, (a, b) -> a < b);
    }

    public boolean isLowerOrEqual(Version v) {
        return compareStatement(v, MAJORS_LESS, (a, b) -> a <= b);
    }

    public boolean isEqual(Version v) {
        return compareStatement(v, MAJORS_EQUAL, (a, b) -> a.equals(b));
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patchToString(patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && Float.compare(version.patch, patch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

}
