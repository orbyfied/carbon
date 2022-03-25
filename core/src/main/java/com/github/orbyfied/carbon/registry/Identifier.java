package com.github.orbyfied.carbon.registry;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Namespaced unique identifier.
 */
public class Identifier implements Cloneable {

    /**
     * Parses a string following {@code namespace:path}
     * into the provided identifier.
     * @param in The input string.
     * @param out The output identifier.
     * @return The identifier.
     * @throws NullPointerException If the input string is null.
     * @throws IllegalArgumentException If the input string is malformed.
     */
    public static Identifier parse(String in, Identifier out) {
        Objects.requireNonNull(in, "input string cannot be null");
        Objects.requireNonNull(out, "output identifier cannot be null");
        String[] components = in.split(":");
        if (components.length < 1)
            throw new IllegalArgumentException("invalid identifier: " + in);
        if (components.length < 2) {
            String[] tmp = new String[2];
            tmp[1] = components[0];
            tmp[0] = null;
            components = tmp;
        }
        out.namespace = components[0];
        out.path      = components[1];
        return out;
    }

    /**
     * Parses a string following {@code namespace:path}
     * into an identifier.
     * @param in The input string.
     * @return The identifier.
     * @throws NullPointerException If the input string is null.
     * @throws IllegalArgumentException If the input string is malformed.
     */
    public static Identifier of(String in) {
        return parse(in, new Identifier());
    }

    ///////////////////////////////

    private String namespace;
    private String path;

    private Identifier() { }

    public Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path      = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    ////////////////////////////

    @Override
    public String toString() {
        return (namespace != null ? namespace : "") + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return Objects.equals(namespace, that.namespace) && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }

    @Override
    public Identifier clone() {
        try {
            return (Identifier) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
