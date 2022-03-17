package com.github.orbyfied.carbon.registry;

public abstract class RegistryService<R extends Registry<T>, T extends RegistryItem> {

    protected final R registry;

    public RegistryService(R registry) {
        this.registry = registry;
    }

    public R getRegistry() {
        return registry;
    }
    
}
