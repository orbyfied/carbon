package net.orbyfied.carbon.bootstrap;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.config.AbstractConfiguration;
import net.orbyfied.carbon.config.Configure;

public class CarbonConfiguration extends AbstractConfiguration {

    protected final Carbon main;

    public CarbonConfiguration(Carbon main) {
        super(main);
        this.main = main;
    }

    @Configure(name = "--config-version")
    protected double configurationVersion;

    public double getConfigurationVersion() {
        return configurationVersion;
    }

    //////////////////////////////////////////////

}
