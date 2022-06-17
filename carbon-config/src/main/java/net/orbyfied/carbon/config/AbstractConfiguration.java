package net.orbyfied.carbon.config;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Abstract implementation of
 * {@link Configuration}
 * @see Configuration
 */
public abstract class AbstractConfiguration implements Configuration {

    /**
     * The configurable that this
     * configuration is for. Can be null.
     */
    protected final Configurable<?> configurable;

    /** Constructor. */
    public AbstractConfiguration(Configurable<?> configurable) {
        this.configurable = configurable;
    }

    /**
     * @see Configuration#getConfigurable()
     */
    @Override
    public Configurable<?> getConfigurable() {
        return configurable;
    }

    // Override save and load to make them final.

    @Override
    public final void save(ConfigurationSection config) {
        Configuration.super.save(config);
    }

    @Override
    public final void load(ConfigurationSection config) {
        Configuration.super.load(config);
    }

}
