package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.Task;

import java.util.function.Consumer;

public class AsyncTask<T, P extends Process<T>> extends Task<T, P> {

    protected boolean isJoined;

    @Override
    public void run(P process, Runnable nextTaskCallback) {
        // get execution service and work executor
        ExecutionService es = process.getExecutionService();
        final Consumer<T> workExec = process.getWorkExecutor();

        // create thread with program
        Thread t = es.newThread(() -> {
            // do work
            for (T w : work)
                workExec.accept(w);

            // call next task if joined
            if (isJoined)
                nextTaskCallback.run();
        });

        // call new task now if not joined
        if (!isJoined)
            nextTaskCallback.run();

        // start the thread to run the program
        t.start();
    }

    @Override
    public void bake(P process) {

    }

    public boolean joined() {
        return isJoined;
    }

    public AsyncTask<T, P> joined(boolean b) {
        this.isJoined = b;
        return this;
    }

    /* --------------------------------- */

    @Override
    public AsyncTask<T, P> addWork(T w) {
        super.addWork(w);
        return this;
    }

    @Override
    public AsyncTask<T, P> removeWork(T w) {
        super.removeWork(w);
        return this;
    }

}
