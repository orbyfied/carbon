package net.orbyfied.carbon.registry;

public interface RegistryService<R extends Registry<T>, T extends Identifiable> {

    R getRegistry();

}
