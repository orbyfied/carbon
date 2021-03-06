package net.orbyfied.carbon.process;

import net.orbyfied.carbon.process.ExecutionService;
import net.orbyfied.carbon.process.ProcessManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BukkitExecutionService extends ExecutionService {

    private final Plugin plugin;

    public BukkitExecutionService(ProcessManager manager, Plugin plugin) {
        super(manager);
        this.plugin = plugin;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }

    @Override
    public void doSync(Runnable r) {
        Bukkit.getScheduler().runTask(plugin, r);
    }

}
