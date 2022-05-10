package net.orbyfied.carbon.registry;

/**
 * A service which should map a specific key
 * type to a registry item.
 * @param <K> The key type.
 * @see RegistryService
 */
public interface MappingService<K, R extends Registry<T>, T extends Identifiable>
        extends RegistryService<R, T> {

    Class<K> getKeyType();

    T getByKey(K key);

}
