package com.github.orbyfied.carbon_core.test.process;

import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.ProcessManager;
import com.github.orbyfied.carbon.process.impl.AsyncTask;
import com.github.orbyfied.carbon.process.impl.QueuedTickExecutionService;
import com.github.orbyfied.carbon.process.impl.SyncTask;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
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

        final BiConsumer<Process<Object>, Object> myWorkExecutor = (p, o) -> System.out.println(o);

        /* Run */

        QueuedTickExecutionService service = new QueuedTickExecutionService(pm);

        Process<Object> myProcess = pm.process(myWorkExecutor)
                .addTasks(
                        new SyncTask<>().addWork(69420),
                        new SyncTask<>().addWork(90000),
                        new SyncTask<>().addWork(42069),
                        new SyncTask<>()
                                .worker((p, t) -> System.out.println("hello"))
                                .addWork(1)
                );

        QueuedTickExecutionService.TickLoop tickLoop = service.tickLoop(true, null);

        myProcess
                .whenDone(tickLoop::end)
                .run(service);

        tickLoop.run();

    }

}
