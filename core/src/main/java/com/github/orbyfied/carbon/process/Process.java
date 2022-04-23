package com.github.orbyfied.carbon.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * A collection of tasks that can be
 * run synchronously, asynchronously,
 * async-distributed across multiple
 * threads, etc.
 * @param <T> The type of work.
 */
public class Process<T> {

    /**
     * The tasks to run.
     */
    protected List<Task<T, Process<T>>> tasks = new ArrayList<>();

    /**
     * The executor of the work.
     */
    protected BiConsumer<Process<T>, T> defaultWorker;

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
     * To run when the task has been
     * completed.
     */
    protected Runnable whenDone;

    /**
     * Constructor.
     * @param manager The parent manager.
     * @param workExecutor The work executor.
     */
    public Process(ProcessManager manager,
                   BiConsumer<Process<T>, T> workExecutor) {
        this.manager      = manager;
        this.defaultWorker = workExecutor;
    }

    public BiConsumer<Process<T>, T> getDefaultWorker() {
        return defaultWorker;
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

    @SafeVarargs
    public final Process<T> addTasks(Task<T, Process<T>>... t) {
        tasks.addAll(Arrays.asList(t));
        return this;
    }

    public Process<T> removeTask(Task<T, Process<T>> task) {
        tasks.remove(task);
        return this;
    }

    public Process<T> whenDone(Runnable r) {
        this.whenDone = r;
        return this;
    }

    public ExecutionService getExecutionService() {
        return executionService;
    }

    public Process<T> setExecutionService(ExecutionService service) {
        this.executionService = service;
        return this;
    }

    public Object getLock() {
        if (lock == null) lock = new Object();
        return lock;
    }

    /**
     * Returns if the task is
     * @return
     */
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
        // mark and notify
        isRunning = false;
        if (lock != null)
            lock.notifyAll();

        // execute callback
        if (whenDone != null)
            whenDone.run();
    }

    /**
     * Starts this process.
     * @return This.
     */
    public Process<T> run() {
        // get execution service
        if (executionService == null)
            executionService = manager.getDefaultExecutionService(this);

        // set that we are running
        isRunning = true;

        // run the first task and start the chain
        runTask(0);

        // return
        return this;
    }

    /**
     * @see Process#setExecutionService(ExecutionService)
     * @see Process#run()
     * @param service The service to run it with.
     * @return This.
     */
    public Process<T> run(ExecutionService service) {
        return setExecutionService(service).run();
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

        // get synchronous thread
        Thread syncthread = Thread.currentThread();

        try {
            // next task call
            Runnable nextTask = () -> runTask(id + 1);

            // run task
            task.run(this, () -> {
                // run synchronously
                Thread ct = Thread.currentThread();
                if (syncthread != ct) {
                    executionService.doSync(nextTask);
                } else {
                    nextTask.run();
                }
            });
        } catch (Exception e) {
            // TODO: proper error handling
            e.printStackTrace();
        }
    }

}
