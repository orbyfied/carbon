package carbon.process.test;

import net.orbyfied.carbon.process.ExecutionService;
import net.orbyfied.carbon.process.Process;
import net.orbyfied.carbon.process.ProcessManager;
import net.orbyfied.carbon.process.impl.ParallelTask;
import net.orbyfied.carbon.process.impl.QueuedTickExecutionService;
import net.orbyfied.carbon.process.impl.SyncTask;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

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
    public void testProcesses1() {

        final BiConsumer<Process<Object>, Object> myWorkExecutor =
                (p, o) -> System.out.println("from " + Thread.currentThread().getName() + ": " + o);

        /* Run */

        QueuedTickExecutionService service = new QueuedTickExecutionService(pm);

        Process<Object> myProcess = pm.process(myWorkExecutor)
                .addTasks(
                        new SyncTask<>().addWork(69420),
                        new SyncTask<>().addWork(90000),
                        new SyncTask<>().addWork(42069),
                        new SyncTask<>()
                                .runnable((p, t) -> System.out.println("hello")),
                        new ParallelTask<>().addWork(1, 2, 3, 4, 5, 6).joined(true),
                        new SyncTask<>().addWork(90000)
                );

        QueuedTickExecutionService.TickLoop tickLoop = service.tickLoop(true, null);

        myProcess
                .whenDone(tickLoop::end)
                .run(service);

        tickLoop.run();

    }

}
