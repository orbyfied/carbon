package com.github.orbyfied.carbon.registry;

public interface FunctionalService<R extends Registry<T>, T extends Identifiable> extends RegistryService<R, T> {

    default void registered(T val) { }

    default void unregistered(T val) { }

}
