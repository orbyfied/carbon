package com.github.orbyfied.carbon;

import com.github.orbyfied.carbon.api.util.Version;
import com.github.orbyfied.carbon.bootstrap.CarbonBootstrap;
import com.github.orbyfied.carbon.command.CommandEngine;
import com.github.orbyfied.carbon.command.impl.BukkitCommandEngine;
import com.github.orbyfied.carbon.content.pack.ResourcePackManager;
import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.core.mod.ModLoader;
import com.github.orbyfied.carbon.logging.BukkitLogger;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.process.ProcessManager;
import com.github.orbyfied.carbon.process.impl.CarbonProcessManager;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.RegistryItem;
import com.github.orbyfied.carbon.user.CarbonUserEnvironment;

import java.nio.file.Path;

/**
 * The carbon plugin and main class.
 */
public class Carbon {

    public static final Version VERSION = Version.of("0.1.0");

    /* ----------- PLUGIN ------------ */

    /**
     * The data folder for Carbon.
     */
    protected final Path directory = Path.of("./.carbon");

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

        /* initialize */
        this.api = new CarbonJavaAPI(this);
        this.registries = new Registry<>("carbon:registries");
        this.modLoader = new ModLoader(this);
        this.processManager = new CarbonProcessManager(this);
        this.resourcePackManager = new ResourcePackManager(this);
        this.commandEngine = new BukkitCommandEngine();
        this.userEnvironment = new CarbonUserEnvironment(this);

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
    protected final Registry<Registry<? extends RegistryItem>> registries;

    /**
     * The main mod loader.
     */
    protected final ModLoader modLoader;

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

    public CarbonJavaAPI getAPI() {
        return api;
    }

    public Registry<Registry<? extends RegistryItem>> getRegistries() {
        return registries;
    }

    public PlatformProxy getPlatform() {
        return platform;
    }

    public ModLoader getModLoader() {
        return modLoader;
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

    public Path getDirectory() { return directory; }

    public Path getFileInDirectory(String n) {
        return directory.resolve(n);
    }

}
