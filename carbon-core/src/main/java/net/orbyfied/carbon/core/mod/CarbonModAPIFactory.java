package net.orbyfied.carbon.core.mod;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.api.CarbonModAPI;

/**
 * Factory for creating and linking or
 * readying a Carbon mod API.
 */
public interface CarbonModAPIFactory<A extends CarbonModAPI> {

    /**
     * Creates a new API for the specified
     * mod if needed. This is only called
     * when an API has not been created yet.
     * @param main The Carbon main instance.
     * @param mod The mod reference.
     * @return The API.
     */
    CarbonModAPI create(final Carbon main, final LoadedMod mod);

    /**
     * If this factory accepts the
     * linking of the provided API.
     * @param main The Carbon main instance.
     * @param mod The mod reference.
     * @param api The API object.
     * @return If it is supported.
     */
    default boolean accepts(final Carbon main, final LoadedMod mod, final CarbonModAPI api) {
        return true;
    }

    /**
     * Links, readies or modifies an
     * existing API. Only works on
     * accepted APIs.
     * @see CarbonModAPIFactory#accepts(Carbon, LoadedMod, CarbonModAPI)
     * @param main The Carbon main instance.
     * @param mod The mod reference.
     * @param api The API instance.
     */
    default void link(final Carbon main, final LoadedMod mod, final CarbonModAPI api) {

    }

    /////////////////////////////////////////////////////////////

    /**
     * Factory for creating {@link CarbonJavaModAPI}
     * instances for mods. This is the default factory.
     */
    CarbonModAPIFactory<CarbonJavaModAPI> JAVA_API_FACTORY =
            (main, mod) -> new CarbonJavaModAPI(mod, main);

}
