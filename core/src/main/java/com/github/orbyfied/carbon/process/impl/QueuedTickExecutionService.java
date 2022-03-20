package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.ProcessManager;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses a queue and a lock to
 * queue up and call tasks.
 */
public class QueuedTickExecutionService extends ExecutionService {

    private Object lock;
    private ArrayDeque<Runnable> tasks;

    public QueuedTickExecutionService(
            ProcessManager manager,
            ArrayDeque<Runnable> tasks,
            Object lock) {
        super(manager);
        this.lock  = lock;
        this.tasks = tasks;
    }

    public QueuedTickExecutionService(
            ProcessManager manager,
            Object lock) {
        super(manager);
        this.lock  = lock;
        this.tasks = new ArrayDeque<>();
    }

    public QueuedTickExecutionService(ProcessManager manager) {
        super(manager);
        this.lock  = new Object();
        this.tasks = new ArrayDeque<>();
    }

    public Object getLock() {
        return lock;
    }

    public ArrayDeque<Runnable> getTasks() {
        return tasks;
    }

    public boolean hasLock() {
        return lock != null;
    }

    public void tickLoop(boolean blocking,
                         Runnable inbetween,
                         AtomicBoolean running) {
        while (running.get()) {
            if (inbetween != null)
                inbetween.run();
            tick();
            if (blocking && lock != null) {
                synchronized (this) {
                    try {
                        lock.wait();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void tick() {
        while (tasks.size() != 0)
            tasks.pop().run();
    }

    @Override
    public void doSync(Runnable r) {
        tasks.add(r);
        if (lock != null)
            lock.notifyAll();
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

}
