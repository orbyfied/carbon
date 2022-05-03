package net.orbyfied.carbon.registry;

public interface MappingService<K, R extends Registry<T>, T extends Identifiable>
        extends RegistryService<R, T> {

    Class<K> getKeyType();

    T getByKey(K key);

}
