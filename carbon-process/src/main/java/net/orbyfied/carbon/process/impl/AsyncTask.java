package net.orbyfied.carbon.process.impl;

import net.orbyfied.carbon.process.ExecutionService;
import net.orbyfied.carbon.process.Process;
import net.orbyfied.carbon.process.Task;

import java.util.List;
import java.util.function.BiConsumer;

public class AsyncTask<T, P extends Process<T>> extends Task<T, P> {

    protected boolean isJoined;

    @Override
    public void run(P process, Runnable nextTaskCallback) {
        // get execution service and work executor
        ExecutionService es = process.getExecutionService();

        // create thread with program
        Thread t = es.newThread(() -> {
            // call new task now if not joined
            if (!isJoined)
                nextTaskCallback.run();

            // run abstract
            if (runAbstract)
                worker.accept(process, null);

            // do work
            for (T w : work)
                worker.accept(process, w);

            // call next task if joined
            if (isJoined)
                nextTaskCallback.run();
        });

        // start the thread to run the program
        t.start();
    }

    @Override
    public void bake(P process) {
        super.bake(process);
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
    public AsyncTask<T, P> addWork(T... w) {
        super.addWork(w);
        return this;
    }

    public AsyncTask<T, P> addWork(List<T> w) {
        super.addWork(w);
        return this;
    }

    @Override
    public AsyncTask<T, P> removeWork(T w) {
        super.removeWork(w);
        return this;
    }

    @Override
    public AsyncTask<T, P> worker(BiConsumer<P, T> worker) {
        super.worker(worker);
        return this;
    }

    @Override
    public AsyncTask<T, P> runnable(BiConsumer<P, T> worker) {
        super.runnable(worker);
        return this;
    }

    @Override
    public AsyncTask<T, P> runAbstract(boolean b) {
        super.runAbstract(b);
        return this;
    }

}
