package net.orbyfied.carbon.core;

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

    public void end() { }

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
