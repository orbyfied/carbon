package net.orbyfied.carbon.util.gui.inventory.scene;

import net.orbyfied.carbon.util.data.ArrayMultiHashMap;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

public class IGItem {

    final String key;

    final ArrayMultiHashMap<Class<?>, IGItemComponent> componentsMapped = new ArrayMultiHashMap<>();
    final ArrayList<IGItemComponent> componentsLinear = new ArrayList<>();

    public IGItem(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /////////////////////////////////////////

    void addComponentClass(Class<?> klass, IGItemComponent component) {
        componentsMapped.addLast(klass, component);
        for (Class<?> next : klass.getClasses())
            if (IGItemComponent.class.isAssignableFrom(next))
                addComponentClass(next, component);
    }

    public <C extends IGItemComponent> IGItem addComponent(C component) {
        componentsLinear.add(component);
        addComponentClass(component.getClass(), component);
        return this;
    }

    public <C extends IGItemComponent> IGItem addComponent(Class<C> cClass,
                                                           Function<IGItem, C> constructor,
                                                           Consumer<C> consumer) {
        C c = constructor.apply(this);
        addComponent(c);
        if (consumer != null)
            consumer.accept(c);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <C extends IGItemComponent> IGItem component(Class<C> cClass,
                                                        Function<IGItem, C> constructor,
                                                        Consumer<C> consumer) {
        ArrayList<IGItemComponent> components;
        if ((components = componentsMapped.getAll(cClass)) != null) {
            for (IGItemComponent component : components)
                consumer.accept((C) component);
            return this;
        }

        C c = constructor.apply(this);
        addComponent(c);
        if (consumer != null)
            consumer.accept(c);
        return this;
    }

}
