package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.core.mod.ModLoader;
import com.github.orbyfied.carbon.element.ModElementRegistry;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.interact.EventItemInteractStrategyService;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.registry.Registry;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The server bootstrap class for
 * Carbon.
 */
public abstract class CarbonBootstrap
        extends JavaPlugin implements Listener {

    /**
     * The instance of this plugin.
     */
    protected static CarbonBootstrap instance;

    /**
     * The main instance.
     */
    protected final Carbon main;

    /**
     * The provided platform proxy.
     * @see CarbonBootstrap#newPlatformProxy()
     */
    protected final PlatformProxy platformProxy;

    /**
     * The BStats metrics.
     */
    protected Metrics metrics;

    /**
     * The BStats plugin ID.
     */
    public final int bStatsPluginId = 14783;

    /** Constructor. */
    public CarbonBootstrap() {
        this.platformProxy = newPlatformProxy();
        this.main          = new Carbon(this, platformProxy);
        this.metrics       = new Metrics(this, bStatsPluginId);
    }

    /**
     * Gets the main instance.
     * @return The main instance.
     */
    public Carbon getMain() {
        return main;
    }

    /**
     * Gets the stored proxy.
     * @return The platform proxy.
     */
    public PlatformProxy getPlatformProxy() {
        return platformProxy;
    }

    /**
     * Get the BStats plugin metrics.
     * @return The metrics object.
     */
    public Metrics getMetrics() {
        return metrics;
    }

    /**
     * Creates a new platform proxy.
     * Only called once.
     * @return The proxy.
     */
    public abstract PlatformProxy newPlatformProxy();

    @Override
    public void onEnable() {

        // send fancy message
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("");
        CarbonBranding.applyForEachLine(
                CarbonBranding.BRAND_ICON_FORMATTED,
                (n, l) -> sender.sendMessage("  " + l + (n == 3 || n == 4 ? "   " + CarbonBranding.BRAND_MESSAGE_FORMATTED : ""))
        );
        sender.sendMessage("");

        // load configuration
        main.getConfigurationHelper()
                .load();

        // start loading mods
        ModLoader loader = main.getModLoader();
        loader.loadAll();

        // load user environment
        main.getUserEnvironment().getCreativeInventoryFactory().enable();

        // prepare to run initialize
        Bukkit.getScheduler().runTaskLater(this, this::initialize, 1);

    }

    @Override
    public void onDisable() {

        // TODO

    }

    /**
     * Initializes all mods and resources.
     */
    public void initialize() {

        // initialize registries
        Registry<CarbonItem<?>> items = new Registry<>("minecraft:items");
        items.addService(new ModElementRegistry<>(items))
                .addService(new CMDRegistryService<>(items));

        main.getRegistries().register(items);

        // initialize all mods
        ModLoader loader = main.getModLoader();
        loader.initializeAll();

        // initialize strategies
        EventItemInteractStrategyService eventItemInteractStrategyService =
                new EventItemInteractStrategyService(main).enable();

        // build and host resource pack
        main.getResourcePackManager()
                .build()
                .whenComplete((mgr, _t) -> mgr.startHost());

    }

}
