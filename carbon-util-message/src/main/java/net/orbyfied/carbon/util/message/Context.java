package net.orbyfied.carbon.util.message;

import net.orbyfied.carbon.util.Provider;

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
    public <T> T rawValue(String key) {
        return (T) values.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T value(String key) {
        Object o = values.get(key);
        if (o instanceof Provider provider) {
            o = provider.provide(this, key);
        }

        return (T) o;
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

    public Context fill(Object... params) {
        if (params.length == 0)
            return this;
        parseContext(this, params);
        return this;
    }

    ////////////////////////////////////////////////////

    public static Context parseContext(Context context,
                                       Object... params) {
        String key = null;
        int l = params.length;

        int p = 0;

        for (int i = 0; i < l; i++) {
            Object o = params[i];
            switch (p) {
                case 0 -> {
                    if (!(o instanceof String))
                        throw new IllegalArgumentException("Expected key at parameter index " + i);
                    key = (String) params[i];
                }

                case 1 -> {
                    // get key type
                    char first = key.charAt(0);
                    key = key.substring(1);

                    switch (first) {
                        case '%' -> { // value
                            context.value(key, o);
                        }

                        case '!' -> { // option
                            context.option(key, o);
                        }
                    }

                    // advance
                    p = 0;
                    continue;
                }
            }

            p++;
        }

        // return
        return context;
    }

}
