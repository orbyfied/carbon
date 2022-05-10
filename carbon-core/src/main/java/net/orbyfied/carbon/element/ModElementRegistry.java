package net.orbyfied.carbon.element;

import net.orbyfied.carbon.registry.AbstractRegistryService;
import net.orbyfied.carbon.registry.FunctionalService;
import net.orbyfied.carbon.registry.MappingService;
import net.orbyfied.carbon.registry.Registry;

/**
 * Registry for IDable mod elements.
 * @param <T> The type of mod element.
 */
public class ModElementRegistry<T extends RegistrableElement>
        extends AbstractRegistryService<Registry<T>, T>
        implements FunctionalService<Registry<T>, T>, MappingService<Integer, Registry<T>, T> {

    public ModElementRegistry(Registry<T> registry) {
        super(registry);
    }

    @Override
    public void registered(T val) {
        val.setId(registry.size() - 1);
        val.setRegistry(registry);
    }

    @Override
    public void unregistered(T val) {
        val.setId(-1);
        val.setRegistry(null);
    }

    @Override
    public Class<Integer> getKeyType() {
        return Integer.class;
    }

    @Override
    public T getByKey(Integer key) {
        return registry.getByIndex(key);
    }
}
