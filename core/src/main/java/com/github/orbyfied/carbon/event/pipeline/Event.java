package com.github.orbyfied.carbon.event.pipeline;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base event class.
 */
public class Event {



    /* ------- Carry System -------- */

    /**
     * The carried objects.
     */
    private Map<String, Object> carrys;

    /**
     * Sets up the carries if it
     * hasn't been initialized yet.
     */
    private void setupCarries() {
        if (carrys == null)
            carrys = new HashMap<>();
    }

    /**
     * Carries a new key-value pair.
     * @param key The key.
     * @param val The value.
     * @return The value for ease of use.
     */
    public <T> T carry(String key, T val) {
        setupCarries();
        carrys.put(key, val);
        return val;
    }

    /**
     * Gets the value corresponding with
     * the specified key. Null if it does
     * not exist.
     * @param key The key.
     * @return The value or null.
     */
    @SuppressWarnings("unchecked")
    public <T> T carried(String key) {
        if (carrys == null) return null;
        return (T) carrys.get(key);
    }

    /**
     * Gets the value corresponding with
     * the specified key. Null if it does
     * not exist.
     * @param key The key.
     * @param tClass The assumed type of the value.
     * @return The value or null.
     */
    @SuppressWarnings("unchecked")
    public <T> T carried(String key, Class<T> tClass) {
        if (carrys == null) return null;
        return (T) carrys.get(key);
    }

    /**
     * Checks if it carries the key.
     * @param key The key.
     * @return True/false.
     */
    public boolean carries(String key) {
        return carrys != null && carrys.containsKey(key);
    }

    /**
     * Returns an immutable map
     * containing all key-value pairs
     * carried.
     * @return The immutable map.
     */
    public Map<String, Object> carried() {
        if (carrys == null) return Map.of();
        return Collections.unmodifiableMap(carrys);
    }

}
