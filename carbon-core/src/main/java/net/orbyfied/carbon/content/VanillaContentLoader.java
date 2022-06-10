package net.orbyfied.carbon.content;

import net.orbyfied.carbon.config.AbstractConfiguration;
import net.orbyfied.carbon.config.Configurable;

public class VanillaContentLoader implements Configurable<VanillaContentLoader.Config> {

    @Override
    public String getConfigurationPath() {
        return null;
    }

    @Override
    public Config getConfiguration() {
        return null;
    }

    static class Config extends AbstractConfiguration {

        public Config(Configurable<?> configurable) {
            super(configurable);
        }

    }

    /////////////////////////////



}
