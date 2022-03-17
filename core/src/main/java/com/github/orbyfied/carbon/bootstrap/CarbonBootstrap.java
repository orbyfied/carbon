package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The server bootstrap class for
 * Carbon.
 */
public abstract class CarbonBootstrap extends JavaPlugin {

    /**
     * The instance of this plugin.
     */
    protected static CarbonBootstrap instance;

    /**
     * The main instance.
     */
    protected final Carbon main;

    protected final PlatformProxy platformProxy;

    public CarbonBootstrap() {
        this.platformProxy = newPlatformProxy();
        this.main          = new Carbon(this, platformProxy);
    }

    public Carbon getMain() {
        return main;
    }

    public PlatformProxy getPlatformProxy() {
        return platformProxy;
    }

    public abstract PlatformProxy newPlatformProxy();

}
