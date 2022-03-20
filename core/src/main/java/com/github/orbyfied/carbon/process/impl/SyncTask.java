package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.Task;

import java.util.function.BiConsumer;

public class SyncTask<T, P extends Process<T>> extends Task<T, P> {

    @Override
    public void run(P process, Runnable nextTaskCallback) {
        // run abstract
        if (runAbstract)
            worker.accept(process, null);

        // do work
        for (T w : work)
            worker.accept(process, w);

        // call next task
        nextTaskCallback.run();
    }

    @Override
    public void bake(P process) {
        super.bake(process);
    }

    /* --------------------------------- */

    @Override
    public SyncTask<T, P> addWork(T w) {
        super.addWork(w);
        return this;
    }

    @Override
    public SyncTask<T, P> addWork(T... w) {
        super.addWork(w);
        return this;
    }

    @Override
    public SyncTask<T, P> removeWork(T w) {
        super.removeWork(w);
        return this;
    }

    @Override
    public SyncTask<T, P> worker(BiConsumer<P, T> worker) {
        super.worker(worker);
        return this;
    }

    @Override
    public SyncTask<T, P> runnable(BiConsumer<P, T> worker) {
        super.runnable(worker);
        return this;
    }

    @Override
    public SyncTask<T, P> runAbstract(boolean b) {
        super.runAbstract(b);
        return this;
    }

}
