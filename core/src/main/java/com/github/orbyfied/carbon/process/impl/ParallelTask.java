package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ParallelTask<T, P extends Process<T>> extends Task<T, P> {

    private boolean isJoined;
    private int threadCount;

    private ArrayList<T>[] workPerThread;

    @Override
    public void run(P process, Runnable nextTaskCallback) {
        ExecutionService es = process.getExecutionService();
        Consumer<T> workExec = process.getWorkExecutor();
        ArrayList<Thread> threads = new ArrayList<>(threadCount);
        AtomicInteger done = new AtomicInteger(0);
        for (int t = 0; t < threadCount; t++) {
            final List<T> workForThread = workPerThread[t];
            Thread thread = es.newThread(() -> {
                for (T w : workForThread)
                    workExec.accept(w);
                done.incrementAndGet();
                if (isJoined) {
                    if (done.get() >= threadCount) {
                        nextTaskCallback.run();
                    }
                }
            });
            threads.add(thread);
        }
        if (!isJoined)
            nextTaskCallback.run();
        for (Thread t : threads)
            t.start();
    }

    @Override
    public void bake(P process) {
        workPerThread = new ArrayList[threadCount];
        // TODO: more efficient distribution
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

}
