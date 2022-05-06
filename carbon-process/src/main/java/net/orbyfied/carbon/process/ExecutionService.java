package net.orbyfied.carbon.process;

public abstract class ExecutionService {

    protected final ProcessManager manager;

    public ExecutionService(ProcessManager manager) {
        this.manager = manager;
    }

    public ProcessManager getManager() {
        return manager;
    }

    public abstract Thread newThread(Runnable r);
    public abstract void   doSync   (Runnable r);

}
