package com.github.orbyfied.carbon.mod;

import com.github.orbyfied.carbon.api.mod.CarbonMod;
import com.github.orbyfied.carbon.platform.ModLoaderProxy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModLoaderProxyImpl implements ModLoaderProxy {

    @Override
    public void resolveModPlugins(List<Plugin> list) {
        list.addAll(
                Arrays.asList(Bukkit.getPluginManager().getPlugins())
                        .stream()
                        .filter(pl -> pl.getClass().isAnnotationPresent(CarbonMod.class))
                        .collect(Collectors.toList()));
    }

}
