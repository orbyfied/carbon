package com.github.orbyfied.carbon.element;

import com.github.orbyfied.carbon.registry.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

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
        System.out.println(val);
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
