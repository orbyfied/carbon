package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.config.AbstractConfiguration;
import com.github.orbyfied.carbon.config.Configure;

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
