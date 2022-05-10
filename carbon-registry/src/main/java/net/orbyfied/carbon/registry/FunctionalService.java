package net.orbyfied.carbon.registry;

/**
 * A service which can add basic functionality
 * to a registry. Has method which are called by
 * the registry as a way to do events.
 * @see RegistryService
 */
public interface FunctionalService<R extends Registry<T>, T extends Identifiable> extends RegistryService<R, T> {

    default void registered(T val) { }

    default void unregistered(T val) { }

}
