package net.orbyfied.carbon.registry;

import net.orbyfied.carbon.util.StringReader;

import java.util.*;

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

        StringReader reader     = new StringReader(in, 0);
        List<String> components = new ArrayList<>();
        char c;
        while ((c = reader.current()) != StringReader.DONE
                && c != '<' && c != '>') {
            components.add(reader.collect(c1 -> c1 != ':' && c1 != '<' && c1 != '>', 1));
        }

        if (components.size() < 1)
            throw new MalformedIdentifierException(in, Identifier.class);
        if (components.size() < 2) {
            String[] tmp = new String[2];
            tmp[1] = components.get(0);
            tmp[0] = null;
            components = Arrays.asList(tmp);
        }

        out.namespace = components.get(0);
        out.path      = components.get(1);
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

    protected String namespace;
    protected String path;

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
        return (namespace != null ? namespace + ":" : "") + path;
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
