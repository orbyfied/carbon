package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.config.AbstractConfiguration;

public class CarbonConfiguration extends AbstractConfiguration {

    protected final Carbon main;

    public CarbonConfiguration(Carbon main) {
        super(main);
        this.main = main;
    }

    //////////////////////////////////////////////

}
