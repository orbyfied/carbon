package net.orbyfied.carbon;

import net.orbyfied.carbon.api.util.Version;
import net.orbyfied.carbon.bootstrap.CarbonBootstrap;
import net.orbyfied.carbon.bootstrap.CarbonConfiguration;
import net.orbyfied.carbon.command.CommandEngine;
import net.orbyfied.carbon.command.impl.BukkitCommandEngine;
import net.orbyfied.carbon.config.Configurable;
import net.orbyfied.carbon.config.ConfigurationHelper;
import net.orbyfied.carbon.content.pack.ResourcePackManager;
import net.orbyfied.carbon.core.CarbonJavaAPI;
import net.orbyfied.carbon.core.ServiceManager;
import net.orbyfied.carbon.core.mod.ModLoader;
import net.orbyfied.carbon.event.ComplexEventBus;
import net.orbyfied.carbon.event.EventBus;
import net.orbyfied.carbon.integration.IntegrationManager;
import net.orbyfied.carbon.logging.BukkitLogger;
import net.orbyfied.carbon.platform.PlatformProxy;
import net.orbyfied.carbon.process.ProcessManager;
import net.orbyfied.carbon.process.CarbonProcessManager;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.user.CarbonUserEnvironment;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Path;

/**
 * The carbon plugin and main class.
 */
public class Carbon
        implements Configurable<CarbonConfiguration> {

//    public static final Version VERSION = Version.readFromResource(Carbon.class, "/version.txt");
    public static final Version VERSION = Version.of("0.1.0");

    /* ----------- PLUGIN ------------ */

    /**
     * The data folder for Carbon.
     */
    protected final Path directory = Path.of("./carbon");

    /**
     * The Carbon plugin instance.
     */
    protected CarbonBootstrap plugin;

    /**
     * Constructor.
     * @param plugin Bootstrap plugin instance.
     */
    public Carbon(CarbonBootstrap plugin, PlatformProxy platform) {
        this.plugin   = plugin;
        this.platform = platform;

        /* initialize core */
        this.configurationHelper = ConfigurationHelper.newYamlFileConfiguration(directory.resolve("config.yml"), "/carbon/config/config.yml");
        this.configuration = new CarbonConfiguration(this);
        configurationHelper.addConfigurable(this);

        this.api = new CarbonJavaAPI(this);
        this.registries = new Registry<>("carbon:registries");
        this.modLoader = new ModLoader(this);
        this.processManager = new CarbonProcessManager(this);
        this.serviceManager = new ServiceManager(this);
        this.coreEventBus = new ComplexEventBus();
        this.resourcePackManager = new ResourcePackManager(this);
        this.commandEngine = new BukkitCommandEngine(this.plugin);
        this.userEnvironment = new CarbonUserEnvironment(this);
        this.integrationManager = new IntegrationManager(this);

    }

    public CarbonBootstrap getPlugin() {
        return plugin;
    }

    /* ------------ MAIN ------------- */

    /**
     * The platform proxy.
     * @see PlatformProxy
     */
    protected final PlatformProxy platform;

    /**
     * The environment API instance.
     */
    protected final CarbonJavaAPI api;

    /**
     * The registered registries. (I know it sounds crazy)
     */
    protected final Registry<Registry<? extends Identifiable>> registries;

    /**
     * The main mod loader.
     */
    protected final ModLoader modLoader;

    /**
     * The main service manager.
     */
    protected final ServiceManager serviceManager;

    /**
     * The main process manager.
     */
    protected final ProcessManager processManager;

    /**
     * The resource pack manager.
     */
    protected final ResourcePackManager resourcePackManager;

    /**
     * The main command engine.
     */
    protected final CommandEngine commandEngine;

    /**
     * The Carbon user environment.
     */
    protected final CarbonUserEnvironment userEnvironment;

    /**
     * The configuration helper.
     */
    protected final ConfigurationHelper<YamlConfiguration> configurationHelper;

    /**
     * The Carbon root configuration.
     */
    protected CarbonConfiguration configuration;

    /**
     * The base, root or core event bus.
     */
    protected final ComplexEventBus coreEventBus;

    /**
     * The stage of initialization Carbon is in.
     */
    protected Object initStage;

    /**
     * The integration manager.
     */
    protected final IntegrationManager integrationManager;

    public CarbonJavaAPI getAPI() {
        return api;
    }

    public Registry<Registry<? extends Identifiable>> getRegistries() {
        return registries;
    }

    public PlatformProxy getPlatform() {
        return platform;
    }

    public ModLoader getModLoader() {
        return modLoader;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public Carbon setInitializationStage(Object o) {
        this.initStage = o;
        return this;
    }

    public Object getInitializationStage() {
        return initStage;
    }

    public ProcessManager getProcessManager() {
        return processManager;
    }

    public ResourcePackManager getResourcePackManager() {
        return resourcePackManager;
    }

    public BukkitLogger getLogger(String id) {
        return new BukkitLogger("Carbon" + id);
    }

    public CommandEngine getCommandEngine() {
        return commandEngine;
    }

    public CarbonUserEnvironment getUserEnvironment() {
        return userEnvironment;
    }

    public ConfigurationHelper<YamlConfiguration> getConfigurationHelper() {
        return configurationHelper;
    }

    public EventBus getCoreEventBus() {
        return coreEventBus;
    }

    public IntegrationManager getIntegrationManager() {
        return integrationManager;
    }

    public Path getDirectory() { return directory; }

    public Path getFileInDirectory(String n) {
        return directory.resolve(n);
    }

    @Override
    public String getConfigurationPath() {
        return "";
    }

    @Override
    public CarbonConfiguration getConfiguration() {
        return configuration;
    }

}
