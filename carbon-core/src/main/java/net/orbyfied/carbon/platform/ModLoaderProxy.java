package net.orbyfied.carbon.platform;

import org.bukkit.plugin.Plugin;

import java.util.List;

public interface ModLoaderProxy {

    void resolveModPlugins(List<Plugin> list);

}
