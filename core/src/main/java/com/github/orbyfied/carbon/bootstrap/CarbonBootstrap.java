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

import java.nio.file.Path;
import java.util.Objects;

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
     * Initialization stage cached for
     * quick access.
     */
    protected InitStage initStage;

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

        // start initialization
        this.initStage = new InitStage().general(InitStageGeneral.ENABLE);

        // send fancy message
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        sender.sendMessage("");
        CarbonBranding.applyForEachLine(
                CarbonBranding.BRAND_ICON_FORMATTED,
                (n, l) -> sender.sendMessage("  " + l + (n == 3 || n == 4 ? "   " + CarbonBranding.BRAND_MESSAGE_FORMATTED : ""))
        );
        sender.sendMessage("");

        // load configuration
        initStage.next(InitStageGeneral.LOAD_CONFIG);
        main.getConfigurationHelper()
                .load();

        // start loading mods
        initStage.next(InitStageGeneral.LOAD_MODS);
        ModLoader loader = main.getModLoader();
        loader.loadAll();

        // load user environment
        initStage.next(InitStageGeneral.LOAD_USER_ENV);
        main.getUserEnvironment().enable();

        // prepare to run initialize
        initStage.next(InitStageGeneral.SCHEDULE_INIT);
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

        initStage.next(InitStageGeneral.INIT);

        // initialize registries
        initStage.next(InitStageGeneral.INIT_REGISTRIES);

        initStage.details("minecraft:items");
        Registry<CarbonItem<?>> itemRegistry = new Registry<>("minecraft:items");
        itemRegistry.addService(new ModElementRegistry<>(itemRegistry))
                .addService(new CMDRegistryService<>(itemRegistry));

        initStage.details("minecraft:recipe_types");
        Registry<RecipeType> recipeTypeRegistry = new Registry<>("minecraft:recipe_types");
        RecipeTypes.registerAll(recipeTypeRegistry);

        initStage.details("minecraft:recipes");
        Registry<Recipe> recipeRegistry = new Registry<>("minecraft:recipes");
        recipeRegistry.addService(new RecipeRegistryService(recipeRegistry));

        initStage.details("Registering...");
        Registry<Registry<? extends Identifiable>> registries = main.getRegistries();

        registries
                .register(itemRegistry)
                .register(recipeTypeRegistry)
                .register(recipeRegistry);

        // initialize services
        initStage.next(InitStageGeneral.INIT_SERVICES);
        main.getServiceManager().initialized();

        // initialize misc apis
        initStage.next(InitStageGeneral.INIT_MISC_APIS);
        initStage.details("CompiledStack: Inject API");
        CompiledStack.initialize(main.getAPI());

        // initialize all mods
        initStage.next(InitStageGeneral.INIT_MODS);
        ModLoader loader = main.getModLoader();
        loader.initializeAll();

        // load everything from registries
        initStage.next(InitStageGeneral.LOADC_REGISTRIES);

        RecipeRegistryService recipeRegistryService = recipeRegistry.getService(RecipeRegistryService.class);
        initStage.details("minecraft:recipe_types : Load workers.");
        for (RecipeType recipeType : recipeTypeRegistry)
            recipeRegistryService.addWorker(recipeType.newWorker());
        initStage.details("minecraft:recipes : Assign workers.");
        for (Recipe recipe : recipeRegistry)
            recipeRegistryService.getWorker(recipe.type()).register(recipe);

        CarbonReport
                .reportFileAndStdout(Path.of("./CarbonCrash.txt"))
                .write()
                .crash();

        // build and host resource pack
        initStage.next(InitStageGeneral.MAKE_RESOURCE_PACK);
        main.getResourcePackManager()
                .build()
                .whenComplete((mgr, _t) -> {
                    mgr.startHost();
                    Bukkit.getScheduler().runTask(this, this::initializeNext);
                });

    }

    /**
     * Second stage of initialization (after resource pack).
     */
    public void initializeNext() {
        // done
        initStage.next(InitStageGeneral.INIT_DONE);
    }

    /**
     * Initialization stage.
     */
    public static class InitStage {

        private InitStageGeneral gen;
        private Object details;

        public InitStageGeneral general() {
            return gen;
        }

        public InitStage general(InitStageGeneral gen) {
            this.gen = gen;
            return this;
        }

        public InitStage next(InitStageGeneral gen) {
            this.details = null;
            this.gen = gen;
            return this;
        }

        public Object details() {
            return details;
        }

        public InitStage details(Object details) {
            this.details = details;
            return this;
        }

        ///////////////////////////////////

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InitStage initStage = (InitStage) o;
            return gen == initStage.gen && Objects.equals(details, initStage.details);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gen, details);
        }

        @Override
        public String toString() {
            return gen + (details != null ? "/[" + details + "]" : "");
        }

    }

    /**
     * Stage Enum (Default Stages)
     */
    public enum InitStageGeneral {

        /**
         * The start/enabling of Carbon.
         */
        ENABLE,

        /**
         * Started loading the Carbon configuration.
         */
        LOAD_CONFIG,

        /**
         * Started loading mods.
         */
        LOAD_MODS,

        /**
         * Started loading user environment.
         */
        LOAD_USER_ENV,

        /**
         * Scheduled initialization.
         */
        SCHEDULE_INIT,

        /**
         * Initialization started.
         */
        INIT,

        /**
         * Started initializing default registries.
         */
        INIT_REGISTRIES,

        /**
         * Initializing service manager and all previously
         * registered services.
         */
        INIT_SERVICES,

        /**
         * Started initializing miscellaneous APIs.
         */
        INIT_MISC_APIS,

        /**
         * Started initializing mods.
         */
        INIT_MODS,

        /**
         * Started loading registry content.
         */
        LOADC_REGISTRIES,

        /**
         * Started making/hosting the resource pack.
         */
        MAKE_RESOURCE_PACK,

        /**
         * Initialization done.
         */
        INIT_DONE

    }

}
