package com.github.orbyfied.carbon.config;

import java.util.function.Function;

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

    ////////////////////////////////////////

    static <C extends Configuration> Configurable<C> of(String path, Function<Configurable<C>, C> config) {
        return new Configurable<>() {
            final C it = config.apply(this);

            @Override
            public String getConfigurationPath() {
                return path;
            }

            @Override
            public C getConfiguration() {
                return it;
            }
        };
    }

}
