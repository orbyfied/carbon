package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.ProcessManager;

import java.util.ArrayDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Uses a queue and a lock to
 * queue up and call tasks.
 */
public class QueuedTickExecutionService extends ExecutionService {

    /**
     * The queue lock.
     * Notified whenever a new
     * task is pushed to the task queue.
     */
    private final Object lock;

    /**
     * The task queue.
     */
    private ArrayDeque<Runnable> tasks;

    /**
     * Constructor.
     * Allows for usage of a custom
     * task queue and lock.
     * @param manager The process manager.
     * @param tasks The task queue to use.
     * @param lock The queue lock to use.
     */
    public QueuedTickExecutionService(
            ProcessManager manager,
            ArrayDeque<Runnable> tasks,
            Object lock) {
        super(manager);
        this.lock  = lock;
        this.tasks = tasks;
    }

    /**
     * Constructor.
     * Allows for usage of a custom
     * lock.
     * @param manager The process manager.
     * @param lock The queue lock to use.
     */
    public QueuedTickExecutionService(
            ProcessManager manager,
            Object lock) {
        super(manager);
        this.lock  = lock;
        this.tasks = new ArrayDeque<>();
    }

    /**
     * Constructor.
     * @param manager The process manager.
     */
    public QueuedTickExecutionService(ProcessManager manager) {
        super(manager);
        this.lock  = new Object();
        this.tasks = new ArrayDeque<>();
    }

    /**
     * Returns the queue lock.
     * @return The lock {@link QueuedTickExecutionService#lock}.
     */
    public Object getLock() {
        return lock;
    }

    /**
     * Returns if this object
     * has a lock.
     * @return True/false.
     */
    public boolean hasLock() {
        return lock != null;
    }

    /**
     * Returns the task queue.
     * @return The queue {@link QueuedTickExecutionService#tasks}.
     */
    public ArrayDeque<Runnable> getTasks() {
        return tasks;
    }

    /**
     * The tick loop process.
     */
    public class TickLoop {

        /**
         * Creates a new tick loop.
         * @param blocking If it should block using
         *                 the queue lock until there
         *                 is new tasks.
         * @param between The code to run between ticks.
         */
        TickLoop(boolean blocking, Runnable between) {
            this.blocking = blocking;
            this.between  = between;
        }

        /**
         * If it is running.
         */
        private final AtomicBoolean running  = new AtomicBoolean(false);

        /**
         * If it should block.
         */
        private final boolean blocking;

        /**
         * The code to run between ticks.
         */
        private final Runnable between;

        /**
         * If it has ended.
         */
        private final AtomicBoolean hasEnded = new AtomicBoolean(false);

        public AtomicBoolean getRunning() { return running; }
        public boolean isBlocking() { return blocking; }

        /**
         * Ends the tick loop.
         * @return This.
         */
        public TickLoop end() {
            // set has ended
            hasEnded.set(true);
            // set not running
            running.set(false);
            if (blocking && lock != null) {
                Thread.currentThread().interrupt();
            }

            // return
            return this;
        }

        /**
         * Reboots the tick loop.
         * Sets the {@link TickLoop#hasEnded} flag
         * to false to allow it to be started again.
         * @return This.
         */
        public TickLoop reboot() {
            hasEnded.set(false);
            return this;
        }

        /**
         * Runs the tick loop.
         * WARN: This is synchronous. It
         * will block the caller thread.
         * @return This.
         */
        public TickLoop run() {
            // checks if it has ended
            if (hasEnded.get())
                return this;
            // sets running to true
            running.set(true);

            // runs the tick loop while running
            while (running.get()) {
                // run between code
                if (between != null)
                    between.run();
                // tick
                tick();

                // wait if it should block
                // make sure the lock exists
                if (blocking && lock != null) {
                    synchronized (lock) {
                        try {
                            // wait
                            lock.wait(250);
                        } catch (InterruptedException e) {
                            // ignore
                        } catch (Exception e) {
                            e.printStackTrace();
                            return this;
                        }
                    }
                }
            }

            // return
            return this;
        }

    }

    /**
     * Creates a tick loop.
     * @see TickLoop#TickLoop(boolean, Runnable)
     * @return The tick loop.
     */
    public TickLoop tickLoop(boolean blocking,
                             Runnable between) {
        return new TickLoop(blocking, between);
    }

    /**
     * Ticks once.
     * Runs all tasks in the
     * queue.
     */
    public void tick() {
        while (tasks.size() != 0)
            tasks.pop().run();
    }

    /**
     * Queues a runnable to
     * be run by a manual ticker
     * or a tick loop.
     * @param r The runnable.
     */
    @Override
    public void doSync(Runnable r) {
        // add to tasks
        tasks.add(r);
        // notify lock if exists
        if (lock != null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
    }

    /**
     * Creates a simple new
     * Java thread.
     * @param r The target.
     * @return The thread.
     */
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

}
