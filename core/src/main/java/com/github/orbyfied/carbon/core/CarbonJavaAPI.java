package com.github.orbyfied.carbon.core;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.api.CarbonAPI;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.Identifiable;

/**
 * The Java plugin Carbon API implementation.
 * See {@link CarbonAPI} for most of the documentation.
 */
public class CarbonJavaAPI implements CarbonAPI {

    /**
     * Reference to the plugin instance.
     */
    private final Carbon plugin;

    /**
     * Constructor. Should not be used
     * by external code.
     * @param plugin The Carbon plugin instance.
     */
    public CarbonJavaAPI(final Carbon plugin) {
        this.plugin = plugin;
    }

    public Registry<Registry<? extends Identifiable>> getRegistries() {
        return plugin.getRegistries();
    }

}
