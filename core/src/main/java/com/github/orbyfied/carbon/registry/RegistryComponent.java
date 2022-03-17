package com.github.orbyfied.carbon.registry;

import java.util.function.Function;

/**
 * An extension to a registry.
 * Allows for more complex storage of items.
 * @param <R> The type of registry.
 * @param <T> The type of base item.
 * @param <K> The keys it stores (for mapping).
 * @param <V> The values it stores (mapping and linear).
 */
public abstract class RegistryComponent<R extends Registry<T>, T extends RegistryItem, K, V> {

    /**
     * The registry this module is
     * applied to.
     */
    protected final R registry;

    /** Constructor. */
    public RegistryComponent(R registry) {
        this.registry = registry;
    }

    /* prefab */
    protected Function<T, K> keyFactory;
    protected Function<T, V> valFactory;
    protected Function<V, K> valKeyFactory;

    /**
     * Gets the registry this component
     * has been assigned to.
     * @return The registry reference.
     */
    public R getRegistry() {
        return registry;
    }

    /**
     * Gets the runtime key type of
     * the storage.
     * @return The key type.
     */
    public abstract Class<K> getKeyType();

    /**
     * Gets the runtime value type
     * of the storage.
     * @return The value type.
     */
    public abstract Class<V> getValueType();

    /**
     * Gets if the storage is linear,
     * so if it is stored in an ordered
     * list.
     * @return If it is.
     */
    public abstract boolean isLinear();

    /**
     * Gets if the storage is mapped,
     * so if it is stored in a quick
     * lookup map.
     * @return If it is.
     */
    public abstract boolean isMapped();

    /**
     * Returns the item to key factory.
     * @return The function.
     */
    public Function<T, K> getKeyFactory() {
        return keyFactory;
    }

    /**
     * Returns the item to value factory.
     * @return The function.
     */
    public Function<T, V> getValueFactory() {
        return valFactory;
    }

    /**
     * Returns the value to key factory.
     * @return The function.
     */
    public Function<V, K> getKeyFromValueFactory() {
        return valKeyFactory;
    }

    /**
     * Utilizes the value to key factory
     * to get the key from the provided value.
     * @param val The value.
     * @return The key.
     */
    public K getKeyFromValue(V val) {
        return getKeyFromValueFactory().apply(val);
    }

    /**
     * Utilizes the item to value factory
     * to get the value of the provided item.
     * @param t The item.
     * @return The value.
     */
    public V getValueOf(T t) {
        return getValueFactory().apply(t);
    }

    /**
     * Utilizes the item to key factory
     * to get the key of the provided item.
     * @param t The item.
     * @return The key.
     */
    public K getKeyOf(T t) {
        return getKeyFactory().apply(t);
    }

    /**
     * Gets the value at the given
     * index.
     * @throws UnsupportedOperationException Might be thrown by an
     *                                       implementation if linear
     *                                       storage is not supported. Check
     *                                       {@link RegistryComponent#isLinear()}
     * @param index The index to use.
     * @return The value.
     */
    public abstract V getLinear(int index);

    /**
     * Gets the value mapped to the
     * given key.
     * @throws UnsupportedOperationException Might be thrown by an
     *                                       implementation if mapped
     *                                       storage is not supported. Check
     *                                       {@link RegistryComponent#isMapped()}
     * @param key The key to look up.
     * @return The value or null.
     */
    public abstract V getMapped(K key);

    /**
     * Gets the value corresponding with
     * the given item.
     * @see RegistryComponent#getMapped(Object)
     * @param key The item.
     * @return The value.
     */
    public V getMapped(T key) {
        return getMapped(keyFactory.apply(key));
    }

    /**
     * Registers a value to the storage.
     * Should utilize the key factory
     * to get the key for mapping.
     * @param value The value to register.
     * @return This.
     */
    public abstract RegistryComponent<R, T, K, V> register(V value);

    /**
     * Utilizes the value factory in the default implementation.
     * @see RegistryComponent#register(Object)
     */
    public RegistryComponent<R, T, K, V> register(T item) {
        return register(getValueFactory().apply(item));
    }

    /**
     * Unregisters a key from the storage.
     * Should utilize the key factory
     * to get the key for mapping.
     * @param key The key to unregister.
     * @return This.
     */
    public abstract RegistryComponent<R, T, K, V> unregister(K key);

    /**
     * Utilizes the key factory in the default implementation.
     * @see RegistryComponent#unregister(Object)
     */
    public RegistryComponent<R, T, K, V> unregister(T item) {
        return unregister(getKeyFactory().apply(item));
    }

}
