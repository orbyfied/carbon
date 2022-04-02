package com.github.orbyfied.carbon.platform.impl;

import com.github.orbyfied.carbon.bootstrap.Carbon1182Bootstrap;
import com.github.orbyfied.carbon.content.pack.ResourcePackProxyImpl;
import com.github.orbyfied.carbon.misc.NetworkProxyImpl;
import com.github.orbyfied.carbon.mod.ModLoaderProxyImpl;
import com.github.orbyfied.carbon.platform.ModLoaderProxy;
import com.github.orbyfied.carbon.platform.NetworkProxy;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.platform.ResourcePackProxy;

/**
 * The 1.18.2 platform proxy implementation.
 */
public class Platform1181 implements PlatformProxy {

    /**
     * The bootstrap plugin reference.
     */
    private final Carbon1182Bootstrap plugin;

    /** Constructor. */
    public Platform1181(Carbon1182Bootstrap plugin) {
        this.plugin = plugin;
    }

    public Carbon1182Bootstrap getPlugin() {
        return plugin;
    }

    private ModLoaderProxyImpl modLoaderProxy = new ModLoaderProxyImpl();
    @Override public ModLoaderProxy getModLoaderProxy() {
        return modLoaderProxy;
    }

    private ResourcePackProxy resourcePackProxy = new ResourcePackProxyImpl();
    @Override public ResourcePackProxy getResourcePackProxy() {
        return resourcePackProxy;
    }

    private NetworkProxy networkProxy = new NetworkProxyImpl();
    @Override public NetworkProxy getNetworkProxy() {
        return networkProxy;
    }

}
