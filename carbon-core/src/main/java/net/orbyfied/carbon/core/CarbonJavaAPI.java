package net.orbyfied.carbon.core;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.api.CarbonAPI;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.util.security.AccessValidator;

/**
 * The Java plugin Carbon API implementation.
 * See {@link CarbonAPI} for most of the documentation.
 */
public class CarbonJavaAPI implements CarbonAPI {

    /**
     * The access validator for checking if a caller can get the main instance.
     */
    private static final AccessValidator ACCESS_VALIDATOR_GET_MAIN = AccessValidator.topInPackage("net.orbyfied.carbon");

    //////////////////////////////////////////////////

    /**
     * Reference to the plugin instance.
     */
    private final Carbon main;

    /**
     * Constructor. Should not be used
     * by external code.
     * @param main The Carbon plugin instance.
     */
    public CarbonJavaAPI(final Carbon main) {
        this.main = main;
        instance = this;
    }

    @Override
    public Carbon getMain() {
        ACCESS_VALIDATOR_GET_MAIN.require(1);
        return main;
    }

    public Registry<Registry<? extends Identifiable>> getRegistries() {
        return main.getRegistries();
    }

    /////////////////////////////////////////////////

    private static CarbonJavaAPI instance;

    @Deprecated
    public static CarbonJavaAPI get() {
        return instance;
    }

}
