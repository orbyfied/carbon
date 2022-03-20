package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.ProcessManager;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    public class TickLoop {

        public TickLoop(boolean blocking, Runnable between) {
            this.blocking = blocking;
            this.between  = between;
        }

        private final AtomicBoolean running  = new AtomicBoolean(false);
        private final boolean       blocking;
        private final Runnable      between;

        public AtomicBoolean getRunning() {
            return running;
        }

        public boolean isBlocking() {
            return blocking;
        }

        public void end() {
            running.set(false);
            if (blocking && lock != null) {
                Thread.currentThread().interrupt();
            }
        }

        public void run() {
            running.set(true);
            while (running.get()) {
                if (between != null)
                    between.run();
                tick();
                if (blocking && lock != null) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            // ignore
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            }
        }

    }

    public TickLoop tickLoop(boolean blocking,
                             Runnable between) {
        return new TickLoop(blocking, between);
    }

    public void tick() {
        while (tasks.size() != 0)
            tasks.pop().run();
    }

    @Override
    public void doSync(Runnable r) {
        tasks.add(r);
        if (lock != null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

}
