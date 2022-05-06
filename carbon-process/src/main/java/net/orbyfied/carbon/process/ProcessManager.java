package net.orbyfied.carbon.process;

import java.util.function.BiConsumer;

public abstract class ProcessManager {

    public abstract ExecutionService getDefaultExecutionService(Process<?> process);

    @SuppressWarnings("unchecked")
    public <T, P extends Process<T>> P process(BiConsumer<P, T> worker) {
        return (P) new Process<T>(this, (BiConsumer<Process<T>, T>)worker);
    }

}
