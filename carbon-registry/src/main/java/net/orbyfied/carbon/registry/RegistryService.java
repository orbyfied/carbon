package net.orbyfied.carbon.registry;

/**
 * A service to add to a registry.
 * By default it doesn't specify any
 * functionality.
 * @param <R> The registry type.
 * @param <T> The registry item type.
 */
public interface RegistryService<R extends Registry<T>, T extends Identifiable> {

    /**
     * Get the registry this service
     * has been applied to.
     * @return The registry.
     */
    R getRegistry();

}
