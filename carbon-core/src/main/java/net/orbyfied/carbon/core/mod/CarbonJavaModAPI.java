package net.orbyfied.carbon.core.mod;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.api.CarbonModAPI;

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
