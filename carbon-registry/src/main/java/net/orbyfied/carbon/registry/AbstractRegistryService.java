package net.orbyfied.carbon.registry;

public abstract class AbstractRegistryService<R extends Registry<T>, T extends Identifiable>
        implements RegistryService<R, T> {

    protected final R registry;

    public AbstractRegistryService(R registry) {
        this.registry = registry;
    }

    @Override
    public R getRegistry() {
        return registry;
    }
    
}
