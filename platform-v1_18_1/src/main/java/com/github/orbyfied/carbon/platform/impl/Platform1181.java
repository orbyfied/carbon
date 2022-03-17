package com.github.orbyfied.carbon.platform.impl;

import com.github.orbyfied.carbon.bootstrap.Carbon1181Bootstrap;
import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.platform.ResourcePackProxy;

/**
 * The 1.18.1 platform proxy implementation.
 */
public class Platform1181 implements PlatformProxy {

    /**
     * The bootstrap plugin reference.
     */
    private final Carbon1181Bootstrap plugin;

    /** Constructor. */
    public Platform1181(Carbon1181Bootstrap plugin) {
        this.plugin = plugin;
    }

    public Carbon1181Bootstrap getPlugin() {
        return plugin;
    }

    @Override
    public ResourcePackProxy getResourcePackProxy() {
        return null;
    }

}
