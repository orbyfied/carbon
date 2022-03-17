package com.github.orbyfied.carbon.element;

import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.RegistryComponent;
import com.github.orbyfied.carbon.registry.RegistryItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Registry for IDable mod elements.
 * @param <T> The type of mod element.
 */
public class ModElementRegistry<T extends RegistrableElement> extends RegistryComponent<Registry<T>, T, Integer, T> {

    private Class<T> valType;

    public ModElementRegistry(Registry<T> registry, Class<T> valType) {
        this(registry);
        this.valType = valType;

        // init factories
        this.keyFactory   = RegistrableElement::getId;
        this.valFactory = item -> item;
        this.valKeyFactory = RegistrableElement::getId;
    }

    /**
     * Constructor.
     */
    public ModElementRegistry(Registry<T> registry) {
        super(registry);
        valType = (Class<T>) RegistrableElement.class;
    }

    private final ArrayList<T> list = new ArrayList<>();

    @Override
    public Class<Integer> getKeyType() {
        return Integer.class;
    }

    @Override
    public Class<T> getValueType() {
        return valType;
    }

    @Override
    public boolean isLinear() {
        return true;
    }

    @Override
    public boolean isMapped() {
        return true;
    }

    @Override
    public T getLinear(int index) {
        return list.get(index);
    }

    @Override
    public T getMapped(Integer key) {
        return list.get(key);
    }

    @Override
    public RegistryComponent<Registry<T>, T, Integer, T> register(T item) {
        int index = list.size();
        list.add(item);
        setIdFieldOn(item, index);
        return this;
    }

    @Override
    public RegistryComponent<Registry<T>, T, Integer, T> unregister(Integer key) {
        list.remove((int) key);
        return this;
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

}
