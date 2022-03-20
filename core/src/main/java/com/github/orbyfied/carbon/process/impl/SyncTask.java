package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.Task;
import com.github.orbyfied.carbon.process.Process;

import java.util.function.Consumer;

public class SyncTask<T, P extends Process<T>> extends Task<T, P> {

    @Override
    public void run(P process, Runnable nextTaskCallback) {
        Consumer<T> workExec = process.getWorkExecutor();
        for (T w : work)
            workExec.accept(w);
        nextTaskCallback.run();
    }

    @Override
    public void bake(P process) {

    }

    /* --------------------------------- */

    @Override
    public SyncTask<T, P> addWork(T w) {
        super.addWork(w);
        return this;
    }

    @Override
    public SyncTask<T, P> removeWork(T w) {
        super.removeWork(w);
        return this;
    }

}
