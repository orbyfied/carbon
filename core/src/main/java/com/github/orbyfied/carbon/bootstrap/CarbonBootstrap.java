package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.core.mod.ModLoader;
import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.RecipeRegistryService;
import com.github.orbyfied.carbon.crafting.type.RecipeType;
import com.github.orbyfied.carbon.crafting.type.RecipeTypes;
import com.github.orbyfied.carbon.element.ModElementRegistry;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CompiledStack;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.registry.Identifiable;
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
        main.getUserEnvironment().enable();

        // prepare to run initialize
        Bukkit.getScheduler().runTaskLater(this, this::initialize, 1);

    }

    // properly disable everything
    // might make testing easier
    // and might fix some other stuff
    @Override
    public void onDisable() {

        // disable user environment
        main.getUserEnvironment().disable();

        // disable all mods
        main.getModLoader().disableAll();

        // disable services
        main.getServiceManager().disable();

    }

    /**
     * Initializes all mods and resources.
     */
    public void initialize() {

        // initialize registries

        Registry<CarbonItem<?>> itemRegistry = new Registry<>("minecraft:items");
        itemRegistry.addService(new ModElementRegistry<>(itemRegistry))
                .addService(new CMDRegistryService<>(itemRegistry));

        Registry<RecipeType> recipeTypeRegistry = new Registry<>("minecraft:recipe_types");
        RecipeTypes.registerAll(recipeTypeRegistry);

        Registry<Recipe> recipeRegistry = new Registry<>("minecraft:recipes");
        recipeRegistry.addService(new RecipeRegistryService(recipeRegistry));

        Registry<Registry<? extends Identifiable>> registries = main.getRegistries();

        registries
                .register(itemRegistry)
                .register(recipeTypeRegistry)
                .register(recipeRegistry);

        // initialize services
        main.getServiceManager().initialized();

        // initialize misc apis
        CompiledStack.initialize(main.getAPI());

        // initialize all mods
        ModLoader loader = main.getModLoader();
        loader.initializeAll();

        // load everything from registries
        RecipeRegistryService recipeRegistryService = recipeRegistry.getService(RecipeRegistryService.class);
        for (RecipeType recipeType : recipeTypeRegistry)
            recipeRegistryService.addWorker(recipeType.newWorker());
        for (Recipe recipe : recipeRegistry)
            recipeRegistryService.getWorker(recipe.type()).register(recipe);

        // build and host resource pack
        main.getResourcePackManager()
                .build()
                .whenComplete((mgr, _t) -> mgr.startHost());

    }

}
