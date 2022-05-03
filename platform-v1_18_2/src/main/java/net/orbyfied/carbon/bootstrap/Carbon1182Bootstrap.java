package net.orbyfied.carbon.bootstrap;

import net.orbyfied.carbon.platform.impl.Platform1181;
import net.orbyfied.carbon.platform.PlatformProxy;

/**
 * The plugin bootstrap for 1.18.2
 * @see CarbonBootstrap
 * @see Platform1181
 */
public class Carbon1182Bootstrap extends CarbonBootstrap {

    @Override
    public PlatformProxy newPlatformProxy() {
        return new Platform1181(this);
    }

}
