package com.github.orbyfied.carbon.process;

import java.util.List;
import java.util.function.Consumer;

/**
 * A collection of tasks that can be
 * run synchronously, asynchronously,
 * async-distributed across multiple
 * threads, etc.
 * @param <T> The type of work.
 */
public class Process<T> {

    /**
     * The executor of the work.
     */
    protected Consumer<T> workExecutor;

    /**
     * The tasks to run.
     */
    protected List<Task<T, Process<T>>> tasks;

    /**
     * The manager of this process.
     */
    protected ProcessManager manager;

    /**
     * The execution service assigned
     * to this process.
     */
    protected ExecutionService executionService;

    /**
     * The lock.
     */
    protected Object lock;

    /**
     * If the task is running.
     */
    protected boolean isRunning;

    /**
     * Constructor.
     * @param manager The parent manager.
     * @param workExecutor The work executor.
     */
    public Process(ProcessManager manager,
                   Consumer<T> workExecutor) {
        this.manager      = manager;
        this.workExecutor = workExecutor;
    }

    public Consumer<T> getWorkExecutor() {
        return workExecutor;
    }

    public List<Task<T, Process<T>>> getTasks() {
        return tasks;
    }

    public ProcessManager getManager() {
        return manager;
    }

    public Process<T> addTask(Task<T, Process<T>> task) {
        tasks.add(task);
        return this;
    }

    public Process<T> removeTask(Task<T, Process<T>> task) {
        tasks.remove(task);
        return this;
    }

    public ExecutionService getExecutionService() {
        return executionService;
    }

    public Object getLock() {
        if (lock == null) lock = new Object();
        return lock;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Waits for the process to complete.
     */
    public void join() {
        lock = new Object();
        try { lock.wait(); }
        catch (Exception e) { }
    }

    /**
     * Waits for the process to complete,
     * or for the timeout to pass.
     * @param timeoutms The timeout in miliseconds.
     */
    public void join(long timeoutms) {
        lock = new Object();
        try { lock.wait(timeoutms); }
        catch (Exception e) { }
    }

    /**
     * Completes the process.
     */
    public void complete() {
        isRunning = false;
        if (lock != null)
            lock.notifyAll();
    }

    /**
     * Starts this process.
     */
    public void run() {
        // get execution service
        executionService = manager.getExecutionService(this);

        // set that we are running
        isRunning = true;

        // run the first task and start the chain
        runTask(0);
    }

    /**
     * Runs the task with the specified
     * ID. If we are starting (id 0) it
     * bakes all tasks first. It will
     * provide next task callbacks to
     * start the chain of tasks.
     * @param id The index to start at.
     */
    private void runTask(int id) {
        // check if the process has been stopped
        if (!isRunning)
            return;

        // if we are starting bake all tasks
        if (id == 0)
            for (Task<T, Process<T>> task : tasks)
                task.bake(this);

        // if we are done with all tasks complete and return
        if (id >= tasks.size()) {
            complete();
            return;
        }

        // get and check task
        Task<T, Process<T>> task = tasks.get(id);
        if (task == null) return;

        try {
            // run task
            task.run(this, () -> runTask(id + 1));
        } catch (Exception e) {
            // TODO: proper error handling
            e.printStackTrace();
        }
    }

}
