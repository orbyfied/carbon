package net.orbyfied.carbon.content;

import net.orbyfied.carbon.config.AbstractConfiguration;
import net.orbyfied.carbon.config.Configurable;

/**
 * Loader for vanilla content definitions.
 */
public class VanillaContentLoader implements Configurable<VanillaContentLoader.Config> {

    /////////////////////////////

    // configuration
    final Config config = new Config(this);

    @Override
    public String getConfigurationPath() {
        return "minecraft-definitions";
    }

    @Override
    public Config getConfiguration() {
        return config;
    }

    static class Config extends AbstractConfiguration {

        public Config(Configurable<?> configurable) {
            super(configurable);
        }

        ////////////////////////



    }

}
