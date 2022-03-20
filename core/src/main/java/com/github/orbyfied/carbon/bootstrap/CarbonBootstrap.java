package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.core.mod.ModLoader;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.util.TextFormatting;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

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

    /** Constructor. */
    public CarbonBootstrap() {
        this.platformProxy = newPlatformProxy();
        this.main          = new Carbon(this, platformProxy);
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

        // start loading mods
        ModLoader loader = main.getModLoader();
        loader.loadAll();

        // prepare to run initialize
        Bukkit.getScheduler().runTaskLater(this, this::initialize, 1);

    }

    /**
     * Initializes all mods and resources.
     */
    public void initialize() {

        // initialize all mods
        ModLoader loader = main.getModLoader();
        loader.initializeAll();

        // build resource pack
        main.getResourcePackManager().build();

    }

}
