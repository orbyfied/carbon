package com.github.orbyfied.carbon_core.test.process;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.ProcessManager;
import com.github.orbyfied.carbon.process.impl.AsyncTask;
import com.github.orbyfied.carbon.process.impl.QueuedTickExecutionService;
import com.github.orbyfied.carbon.process.impl.SyncTask;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MultiTaskProcessTests {

    private static final ExecutionService es;

    private static final ProcessManager pm = new ProcessManager() {
        @Override
        public ExecutionService getDefaultExecutionService(Process<?> process) {
            return es;
        }
    };

    static {
        es = new ExecutionService(pm) {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }

            @Override
            public void doSync(Runnable r) {
                throw new UnsupportedOperationException();
            }
        };
    }

    /////////////////////////////////////////////

    /* ---- 1 ---- */

    @Test
    public void testSyncProcess() {

        final Consumer<Object> myWorkExecutor = System.out::println;

        /* Run */

        QueuedTickExecutionService service = new QueuedTickExecutionService(pm);

        Process<Object> myProcess = new Process<>(pm, myWorkExecutor)
                .addTask(
                        new SyncTask<>().addWork(69420)
                ).addTask(
                        new AsyncTask<>().addWork(90000).joined(true)
                ).addTask(
                        new SyncTask<>().addWork(42069)
                );

        myProcess.run();

        AtomicBoolean running = new AtomicBoolean(); // for terminating
        service.tickLoop(true, null, running);

    }

}
