package com.github.orbyfied.carbon.process.impl;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.process.ExecutionService;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.ProcessManager;

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
