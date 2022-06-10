package net.orbyfied.carbon.bootstrap;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.block.CarbonBlock;
import net.orbyfied.carbon.block.CarbonBlockState;
import net.orbyfied.carbon.content.BehaviourAdapter;
import net.orbyfied.carbon.content.CMDRegistryService;
import net.orbyfied.carbon.core.ServiceManager;
import net.orbyfied.carbon.core.mod.ModLoader;
import net.orbyfied.carbon.crafting.Recipe;
import net.orbyfied.carbon.crafting.RecipeRegistryService;
import net.orbyfied.carbon.crafting.type.RecipeType;
import net.orbyfied.carbon.crafting.type.RecipeTypes;
import net.orbyfied.carbon.element.ModElementRegistry;
import net.orbyfied.carbon.integration.Integration;
import net.orbyfied.carbon.integration.IntegrationManager;
import net.orbyfied.carbon.integration.impl.PapiIntegration;
import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;
import net.orbyfied.carbon.item.CompiledStack;
import net.orbyfied.carbon.item.ItemFixer;
import net.orbyfied.carbon.item.behaviour.ItemBehaviourAdapter;
import net.orbyfied.carbon.logging.BukkitLogger;
import net.orbyfied.carbon.platform.PlatformProxy;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.message.Message;
import net.orbyfied.carbon.util.message.slice.Literal;
import net.orbyfied.carbon.util.message.slice.Placeholder;
import net.orbyfied.carbon.util.message.style.Style;
import net.orbyfied.carbon.util.message.style.color.Shaders;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The server bootstrap class for
 * Carbon.
 */
public abstract class CarbonBootstrap
        extends JavaPlugin implements Listener {

    /**
     * List of default integrations to try and load.
     */
    public static final List<Class<? extends Integration>> DEFAULT_INTEGRATION_CLASSES = Arrays.asList(
            PapiIntegration.class // placeholder api
    );

    /**
     * List of API classes that should be initialized
     * with dependency references.
     */
    private static final List<Class<?>> API_CLASSES = Arrays.asList(
            // misc
            CompiledStack.class,

            // state classes
            CarbonBlockState.class,
            CarbonItemState.class,

            // behaviour adapters
            BehaviourAdapter.class,
            ItemBehaviourAdapter.class
    );

    /**
     * The instance of this plugin.
     */
    protected static CarbonBootstrap instance;

    /**
     * Logger for bootstrapping information.
     */
    protected final BukkitLogger boostrapLogger;

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
        this.main = new Carbon(this, platformProxy);
        this.metrics = new Metrics(this, bStatsPluginId);

        this.initStage = new InitStage();
        main.setInitializationStage(initStage);

        this.boostrapLogger = main.getLogger("CarbonBoostrap");

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
        
        try {

            // start initialization
            initStage.next(InitStageGeneral.ENABLE);

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

            // load integrations
            initStage.next(InitStageGeneral.LOAD_INTEGRATIONS);
            IntegrationManager im = main.getIntegrationManager();
            for (Class<? extends Integration> ingKlass : DEFAULT_INTEGRATION_CLASSES)
                im.constructAndRegister(ingKlass);

            // load user environment
            initStage.next(InitStageGeneral.LOAD_USER_ENV);
            main.getUserEnvironment().enable();

            // prepare to run initialize
            initStage.next(InitStageGeneral.SCHEDULE_INIT);
            Bukkit.getScheduler().runTaskLater(this, this::initialize, 1);

        } catch (Exception e) {

            // print error
            boostrapLogger.err("Critical exception while loading Carbon:");
            boostrapLogger.errc(e);

            // report exception
            CarbonReport.reportFile()
                    .setMessage("Critical exception while loading Carbon")
                    .setProperty("Boostrap Class", this.getClass())
                    .withError(e)
                    .write()
                    .disabled();

        }
        
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

        // disable all api classes
        for (Class<?> klass : API_CLASSES)
            disableAPIClass(klass);

        // disable all integrations
        main.getIntegrationManager().disable();

        // disable services
        main.getServiceManager().disable();

    }

    /**
     * Initializes all mods and resources.
     */
    public void initialize() {

        try {

            initStage.next(InitStageGeneral.INIT);

            // initialize registries
            initStage.next(InitStageGeneral.INIT_REGISTRIES);

            initStage.details("minecraft:items");
            Registry<CarbonItem<?>> itemRegistry = new Registry<>("minecraft:items");
            itemRegistry.addService(new ModElementRegistry<>(itemRegistry))
                    .addService(new CMDRegistryService<>(itemRegistry));

            initStage.details("minecraft:blocks");
            Registry<CarbonBlock<?>> blockRegistry = new Registry<>("minecraft:blocks");
            blockRegistry.addService(new ModElementRegistry<>(blockRegistry));

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
                    .register(blockRegistry)
                    .register(recipeTypeRegistry)
                    .register(recipeRegistry);

            // initialize services
            initStage.next(InitStageGeneral.INIT_SERVICES);
            final ServiceManager sm = main.getServiceManager();
            sm.initialized();

            // initialize misc apis
            initStage.next(InitStageGeneral.INIT_MISC_APIS);
            initStage.details("Initialize API classes");
            for (Class<?> apiKlass : API_CLASSES)
                initializeAPIClass(apiKlass);
            initStage.details("ItemFixer: Register Service");
            sm.addService(new ItemFixer(sm));

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

            // enable integrations
            initStage.next(InitStageGeneral.INIT_INTEGRATIONS);
            IntegrationManager im = main.getIntegrationManager();
            im.enable();

            // build and host resource pack
            initStage.next(InitStageGeneral.MAKE_RESOURCE_PACK);
            main.getResourcePackManager()
                    .build()
                    .whenComplete((mgr, _t) -> {
                        mgr.startHost();
                        Bukkit.getScheduler().runTask(this, this::initializeNext);
                    });

        } catch (Exception e) {

            // print error
            boostrapLogger.err("Critical exception while initializing Carbon:");
            boostrapLogger.errc(e);

            // report exception
            CarbonReport.reportFile()
                    .setMessage("Critical exception while initializing Carbon")
                    .setProperty("Boostrap Class", this.getClass())
                    .withError(e)
                    .write()
                    .disabled();

        }

    }

    /**
     * Initializes a single API class.
     * @param klass The API class.
     */
    public void initializeAPIClass(Class<?> klass) {
        // set stage
        initStage.details("Initialize API Class " + klass.getSimpleName());

        try {

            // call initialize method
            Method method = klass.getDeclaredMethod("initializeApi", CarbonAPI.class);
            method.setAccessible(true);

            // invoke
            method.invoke(null, main.getAPI());

        } catch (NoSuchMethodException ignore) {
            // ignore
        } catch (Exception e) {
            // pass through exception
            // to trigger report
            throw new RuntimeException(e);
        }
    }

    /**
     * Disables a single API class.
     * @param klass The API class.
     */
    public void disableAPIClass(Class<?> klass) {
        try {

            // call initialize method
            Method method = klass.getDeclaredMethod("disableApi", CarbonAPI.class);
            method.setAccessible(true);

            // invoke
            method.invoke(null, main.getAPI());

        } catch (NoSuchMethodException ignore) {
            // ignore
        } catch (Exception e) {
            // pass through exception
            throw new RuntimeException(e);
        }
    }

    /**
     * Second stage of initialization (after resource pack).
     */
    public void initializeNext() {

        try {

            // done
            initStage.next(InitStageGeneral.INIT_DONE);

        } catch (Exception e) {

            // print error
            boostrapLogger.err("Critical exception while initializing Carbon:");
            boostrapLogger.errc(e);

            // report exception
            CarbonReport.reportFile()
                    .setMessage("Critical exception while initializing Carbon")
                    .setProperty("Boostrap Class", this.getClass())
                    .withError(e)
                    .write();

        }

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
            return gen + (details != null ? ": " + details + "" : "");
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
         * Started loading/registering integrations.
         */
        LOAD_INTEGRATIONS,

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
         * Started initializing/enabling integrations.
         */
        INIT_INTEGRATIONS,

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
