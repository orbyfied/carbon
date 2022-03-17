package com.github.orbyfied.carbon;

import com.github.orbyfied.carbon.bootstrap.CarbonBootstrap;
import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.registry.Registry;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The carbon plugin and main class.
 */
public class Carbon
        extends JavaPlugin {

    /* ----------- PLUGIN ------------ */

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
    protected final CarbonJavaAPI api = new CarbonJavaAPI(this);

    /**
     * The registered registries. (I know it sounds crazy)
     */
    protected final Registry<Registry<?>> registries = new Registry<>("carbon:registries");

    public CarbonJavaAPI getAPI() {
        return api;
    }

    public Registry<Registry<?>> getRegistries() {
        return registries;
    }

    public PlatformProxy getPlatform() {
        return platform;
    }

}
