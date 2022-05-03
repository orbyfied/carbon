package net.orbyfied.carbon.api;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Registry;

/**
 * The abstract Carbon API.
 */
public interface CarbonAPI {

    /**
     * Return the main instance of Carbon.
     * Should be secured.
     * @return The instance.
     */
    Carbon getMain();

    /**
     * Gets all registered registries.
     * These can be for content, configurations,
     * systems, etc. Anything with 'carbon:*' is
     * a Carbon system registry. Most 'minecraft:*'
     * registries are for content.
     * @return The registry of registries.
     */
    Registry<Registry<? extends Identifiable>> getRegistries();

}
