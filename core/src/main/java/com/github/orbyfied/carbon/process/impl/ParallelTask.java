package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ParallelTask<T, P extends Process<T>> extends Task<T, P> {

    private boolean isJoined;
    private int threadCount;

    private ArrayList<T>[] workPerThread;

    @Override
    public void run(P process, Runnable nextTaskCallback) {
        // get execution service
        ExecutionService es = process.getExecutionService();

        // threads created and to be run
        ArrayList<Thread> threads = new ArrayList<>(threadCount);
        // done counter
        AtomicInteger done = new AtomicInteger(0);
        for (int t = 0; t < threadCount; t++) {
            // get work for the thread
            final List<T> workForThread = workPerThread[t];

            // create thread
            Thread thread = es.newThread(() -> {
                // do work
                for (T w : workForThread)
                    worker.accept(process, w);

                // mark as done
                done.incrementAndGet();
                // run next task if all are done
                if (isJoined) {
                    if (done.get() >= threadCount) {
                        nextTaskCallback.run();
                    }
                }
            });

            // add thread
            threads.add(thread);
        }

        // run next task now if not joined
        if (!isJoined)
            nextTaskCallback.run();

        // start all threads
        for (Thread t : threads)
            t.start();
    }

    @Override
    public void bake(P process) {
        super.bake(process);

        // distribute work over all threads
        // TODO: more efficient distribution
        workPerThread = new ArrayList[threadCount];
        for (int i = 0; i < work.size(); i++) {
            int g = i % threadCount;
            ArrayList<T> l = workPerThread[g];
            if (l == null) {
                l = new ArrayList<>();
                workPerThread[g] = l;
            }
            l.add(work.get(i));
        }
    }

    public boolean joined() {
        return isJoined;
    }

    public ParallelTask<T, P> joined(boolean b) {
        this.isJoined = b;
        return this;
    }

    public int threads() {
        return threadCount;
    }

    public ParallelTask<T, P> threads(int c) {
        this.threadCount = c;
        return this;
    }

    /* --------------------------------- */

    @Override
    public ParallelTask<T, P> addWork(T w) {
        super.addWork(w);
        return this;
    }

    @Override
    public ParallelTask<T, P> removeWork(T w) {
        super.removeWork(w);
        return this;
    }

    @Override
    public ParallelTask<T, P> worker(BiConsumer<P, T> worker) {
        super.worker(worker);
        return this;
    }

}
