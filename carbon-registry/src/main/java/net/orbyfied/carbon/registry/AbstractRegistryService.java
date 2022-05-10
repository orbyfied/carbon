package net.orbyfied.carbon.registry;

/**
 * Abstract implementation of a registry service,
 * with a constructor supplying it with a registry.
 * @see RegistryService
 */
public abstract class AbstractRegistryService<R extends Registry<T>, T extends Identifiable>
        implements RegistryService<R, T> {

    /**
     * The registry this service
     * has been applied to.
     */
    protected final R registry;

    public AbstractRegistryService(R registry) {
        this.registry = registry;
    }

    @Override
    public R getRegistry() {
        return registry;
    }
    
}
