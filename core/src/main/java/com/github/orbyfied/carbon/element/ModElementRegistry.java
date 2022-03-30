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
        setIdFieldOn(val, registry.size() - 1);
    }

    @Override
    public void unregistered(T val) {
        setIdFieldOn(val, -1);
    }

    private static void setIdFieldOn(RegistrableElement elem, int to) {
        try {
            idField.set(elem, to);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Field idField;

    static {
        Field idF = null;
        try {
            idF = RegistrableElement.class.getDeclaredField("id");
            idF.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        idField = idF;
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
