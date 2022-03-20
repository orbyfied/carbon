package com.github.orbyfied.carbon.process;

import com.github.orbyfied.carbon.logging.BukkitLogger;

public abstract class ProcessManager {

    public BukkitLogger getLogger() {
        // TODO
        return null;
    }

    public abstract ExecutionService getExecutionService(Process<?> process);

}
