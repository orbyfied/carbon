package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.platform.PlatformProxy;
import com.github.orbyfied.carbon.platform.impl.Platform1181;

/**
 * The plugin bootstrap for 1.18.1
 * @see CarbonBootstrap
 * @see com.github.orbyfied.carbon.platform.impl.Platform1181
 */
public class Carbon1181Bootstrap extends CarbonBootstrap {

    @Override
    public PlatformProxy newPlatformProxy() {
        return new Platform1181(this);
    }

}
