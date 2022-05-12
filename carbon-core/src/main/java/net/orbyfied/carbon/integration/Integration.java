package net.orbyfied.carbon.integration;

import net.orbyfied.carbon.Carbon;

public abstract class Integration {

    protected final Carbon main;
    protected final IntegrationManager manager;

    protected final String id;
    protected final String name;

    protected boolean enabled;

    public Integration(Carbon main,
                       IntegrationManager manager,
                       String id,
                       String name) {
        this.main    = main;
        this.manager = manager;

        this.id   = id;
        this.name = name;
    }

    public Carbon getMain() {
        return main;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    ///////////////////////////////////////

    protected abstract boolean isAvailable();

    protected abstract void load();

    protected abstract void enable();

    protected abstract void disable();

}
