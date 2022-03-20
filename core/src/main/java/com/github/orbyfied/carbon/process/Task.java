package com.github.orbyfied.carbon.process;

import java.util.ArrayList;
import java.util.List;

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
    protected final List<T> work = new ArrayList<>();

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
    public abstract void bake(final P process);

    /* Work Modification */

    public List<T> getWork() {
        return work;
    }

    public Task<T, P> addWork(T w) {
        work.add(w);
        return this;
    }

    public Task<T, P> removeWork(T w) {
        work.remove(w);
        return this;
    }

}
