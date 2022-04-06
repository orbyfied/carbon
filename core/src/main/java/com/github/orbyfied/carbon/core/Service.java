package com.github.orbyfied.carbon.core;

import com.github.orbyfied.carbon.registry.Identifier;

public abstract class Service {

    protected final ServiceManager manager;

    protected boolean init;

    public Service(ServiceManager manager) {
        this.manager = manager;
    }

    public ServiceManager getManager() {
        return manager;
    }

    protected abstract void start();

    public void initialize() {
        try {
            start();
            init = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean wasInitialized() {
        return init;
    }

}
