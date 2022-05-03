package net.orbyfied.carbon.process.impl;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.process.ExecutionService;
import net.orbyfied.carbon.process.Process;
import net.orbyfied.carbon.process.ProcessManager;

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
