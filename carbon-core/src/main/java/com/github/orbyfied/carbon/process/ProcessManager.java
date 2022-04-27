package com.github.orbyfied.carbon.process;

import com.github.orbyfied.carbon.logging.BukkitLogger;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ProcessManager {

    public BukkitLogger getLogger() {
        // TODO
        return null;
    }

    public abstract ExecutionService getDefaultExecutionService(Process<?> process);

    @SuppressWarnings("unchecked")
    public <T, P extends Process<T>> P process(BiConsumer<P, T> worker) {
        return (P) new Process<T>(this, (BiConsumer<Process<T>, T>)worker);
    }

}
