package com.github.orbyfied.carbon.config;

/**
 * Represents a component that can
 * be configured using a configuration.
 */
public interface Configurable<C extends Configuration> {

    /**
     * Get the configuration path from
     * the root to here.
     * @return The configuration path.
     */
    String getConfigurationPath();

    /**
     * Get the configuration.
     * @return The configuration.
     */
    C getConfiguration();

}
