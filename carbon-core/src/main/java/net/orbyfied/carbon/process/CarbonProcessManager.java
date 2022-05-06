package net.orbyfied.carbon.process;

import net.orbyfied.carbon.Carbon;

public class CarbonProcessManager extends ProcessManager {

    private Carbon main;
    private BukkitExecutionService executionService;

    public CarbonProcessManager(Carbon main) {
        this.main = main;
        this.executionService = new BukkitExecutionService(this, main.getPlugin());
    }

    @Override
    public ExecutionService getDefaultExecutionService(Process<?> process) {
        return executionService;
    }

}
