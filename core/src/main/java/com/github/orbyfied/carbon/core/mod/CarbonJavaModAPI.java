package com.github.orbyfied.carbon.core.mod;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.api.CarbonAPI;
import com.github.orbyfied.carbon.api.CarbonModAPI;

public class CarbonJavaModAPI extends CarbonModAPI {

    /**
     * Constructor.
     * @param mod The mod reference.
     * @param main The Carbon main instance.
     */
    public CarbonJavaModAPI(LoadedMod mod, Carbon main) {
        super(mod, main);
    }

}
