package com.github.orbyfied.carbon.element;

import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.Identifiable;

public abstract class RegistrableElement implements Identifiable {

    private Registry<? extends RegistrableElement> registry;
    private int id;

    public int getId() {
        return id;
    }

    public Registry<? extends RegistrableElement> getRegistry() {
        return registry;
    }

}
