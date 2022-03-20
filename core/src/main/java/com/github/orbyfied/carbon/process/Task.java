package com.github.orbyfied.carbon.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * One runnable part in a chain
 * of a process. Abstract to allow
 * for multiple implementations, like
 * asynchronous tasks.
 * @param <T> The work type.
 * @param <P> The process type.
 */
public abstract class Task<T, P extends Process<T>> {

    /**
     * The initial work it will have to do.
     * Undistributed, simple linear list.
     */
    protected List<T> work = new ArrayList<>();

    /**
     * If the worker should run once before
     * it runs the work with null as parameter.
     */
    protected boolean runAbstract = false;

    /**
     * The work executor.
     */
    protected BiConsumer<P, T> worker;

    /**
     * Runs this task. Provides the process
     * and a callback to run the next task.
     * @param process The process it was called by.
     * @param nextTaskCallback The callback for moving on.
     */
    public abstract void run(final P process,
                             final Runnable nextTaskCallback);

    /**
     * Is supposed to bake any information
     * that might be used a lot before the
     * task is run. Usually called before the
     * start of a process.
     * @param process The process that called this.
     */
    @SuppressWarnings("unchecked")
    public void bake(final P process) {
        if (worker == null)
            worker = (BiConsumer<P, T>) process.getDefaultWorker();
    }

    /* Work Modification */

    public List<T> getWork() {
        return work;
    }

    public Task<T, P> addWork(T w) {
        work.add(w);
        return this;
    }

    public Task<T, P> addWork(T... w) {
        work.addAll(Arrays.asList(w));
        return this;
    }

    public Task<T, P> removeWork(T w) {
        work.remove(w);
        return this;
    }

    public Task<T, P> worker(BiConsumer<P, T> worker) {
        this.worker = worker;
        return this;
    }

    public Task<T, P> runnable(BiConsumer<P, T> worker) {
        this.runAbstract = true;
        this.worker = worker;
        return this;
    }

    public Task<T, P> runAbstract(boolean b) {
        this.runAbstract = b;
        return this;
    }

    public boolean runAbstract() {
        return this.runAbstract;
    }

}
