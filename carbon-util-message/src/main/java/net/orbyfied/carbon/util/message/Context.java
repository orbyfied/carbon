package net.orbyfied.carbon.util.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Context {

    /**
     * The placeholder values for this context.
     */
    protected final Map<String, Object> values = new HashMap<>();

    /**
     * The options for this operation.
     */
    protected final Map<String, Object> options = new HashMap<>();

    /**
     * The depth into the operation we are.
     */
    protected int depth = 0;

    /* ---- Basic Context Manipulation ---- */

    public Context value(String key, Object v) {
        values.put(key, v);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T value(String key) {
        return (T) values.get(key);
    }

    public Context option(String key, Object v) {
        options.put(key, v);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T option(String key) {
        return (T) options.get(key);
    }

    public int depth() {
        return depth;
    }

    public Context descend() {
        depth++;
        return this;
    }

    public Context ascend() {
        depth--;
        return this;
    }

}
